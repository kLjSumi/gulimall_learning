package com.atguigu.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.gulimall.product.service.CategoryBrandRelationService;
import com.atguigu.gulimall.product.vo.Catalog2Vo;
import com.mysql.cj.util.TimeUtil;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Resource
    private CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {

        //查出所有分类
        List<CategoryEntity> entities = baseMapper.selectList(null);
        System.out.println(entities);

        List<CategoryEntity> level1Menus = entities.stream().filter(categoryEntity ->
                categoryEntity.getParentCid() == 0
        ).map((menu) -> {
            menu.setChildren(getChildren(menu, entities));
            return menu;
        }).sorted((menu1, menu2) -> {
            //菜单的排序
            return menu1.getSort() - menu2.getSort();
        }).collect(Collectors.toList());
//        System.out.println(level1Menus);
        return level1Menus;
    }

    @Override
    public void removeMenuByIds(List<Long> catIds) {
        //TODO 检查当前删除的菜单，是否被别的地方引用，

        baseMapper.deleteBatchIds(catIds);
    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> path = new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, path);
        Collections.reverse(parentPath);
        return path.toArray(new Long[0]);
    }

    /**
     * 级联更新
     *
     * @param category
     */
    @CacheEvict(value = "category", key = "getLevel1Category")
    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        if (!StringUtils.isEmpty(category.getName())) {
            categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
        }
    }

    //每一个需要缓存的数据我们都来指定要放到哪一个名字的缓存，（缓存的分区）
    @Cacheable(value = {"category"}, key = "#root.method.name")  //代表当前方法的结果需要缓存，如果缓存中有，方法不调用。如果缓存中没有，会调用方法，最后将方法的返回值保存进缓存中
    @Override
    public List<CategoryEntity> getLevel1Category() {
        System.out.println("添加缓存");
        List<CategoryEntity> entities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        return entities;
    }

    // 会产生堆外内存溢出 lettuce 5.2.2.RELEASE解决了这个问题
    //springboot2.0以后默认使用lettuce作为操作redis客户端，它使用netty进行网络通信
    //lettuce的bug导致netty堆外内存溢出 -Xmx100m netty如果没有指定堆外内存，默认只用这个
    //可以通过-Dio.netty.maxDirectMemory进行设置（不太行）
    /**
     * 1、空结果缓存：解决缓存穿透
     * 2、设置过期时间（加随机值）：解决缓存雪崩
     * 3、加锁：解决缓存击穿
     *
     * @return
     */
//    @Cacheable({"catalogJson"})
    @Override
    public Map<String, List<Catalog2Vo>> getCatalogJson() {
        // 加入缓存逻辑, 缓存中存的数据是json字符串
        String catalogJson = stringRedisTemplate.opsForValue().get("catalogJson");
        if (StringUtils.isEmpty(catalogJson)) {
            //缓存中没有对应数据
            //从数据查询
            System.out.println("缓存未命中");
            Map<String, List<Catalog2Vo>> catalogJsonFromDB = null;
            try {
                catalogJsonFromDB = getCatalogJsonFromDBWithRedissonLock();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return catalogJsonFromDB;
        }
        System.out.println("缓存命中");
        Map<String, List<Catalog2Vo>> result = JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catalog2Vo>>>() {
        });
        return result;
    }

    /**
     * 从数据库查询分类数据
     * 使用本地锁
     *
     * @return
     */
    public Map<String, List<Catalog2Vo>> getCatalogJsonFromDBWithLocalLock() {

        synchronized (this) {
            String catalogJson = stringRedisTemplate.opsForValue().get("catalogJson");
            if (!StringUtils.isEmpty(catalogJson)) {
                Map<String, List<Catalog2Vo>> result = JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catalog2Vo>>>() {
                });
                return result;
            }
            Map<String, List<Catalog2Vo>> catalogJsonFromDB = getCatalogJsonFromDB();
            String s = JSON.toJSONString(catalogJsonFromDB);
            stringRedisTemplate.opsForValue().set("catalogJson", s, 1, TimeUnit.DAYS);
            return catalogJsonFromDB;
        }
    }

    /**
     * 从数据库查询分类数据
     * 使用Redisson分布式锁
     *
     * @return
     */
    public Map<String, List<Catalog2Vo>> getCatalogJsonFromDBWithRedissonLock() {
        RLock lock = redissonClient.getLock("catalogJson-lock");
        lock.lock();

        Map<String, List<Catalog2Vo>> dataFromDb;
        try {
            dataFromDb = getCatalogJsonFromDB();
        } finally {
            lock.unlock();
        }
        return dataFromDb;

    }

    /**
     * 从数据库查询分类数据
     * 使用redis锁
     * @return
     */
    public Map<String, List<Catalog2Vo>> getCatalogJsonFromDBWithRedisLock() throws InterruptedException {

        //1、占分布式锁
        String uuid = UUID.randomUUID().toString();
        Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent("lock", "uuid", 300, TimeUnit.SECONDS);
        if (lock) {
            Map<String, List<Catalog2Vo>> catalogJsonFromDB = null;
            try {
                //执行业务逻辑
                catalogJsonFromDB = getCatalogJsonFromDB();

            } finally {
                //删除锁
//            stringRedisTemplate.delete("lock");
//            String lock1 = stringRedisTemplate.opsForValue().get("lock");
//            if (uuid.equals(lock1)) {
//                //删除自己的锁
//                stringRedisTemplate.delete("lock");
//            }
                String script = "if redis.call(\"get\",KEYS[1]) == ARGV[1]\n" +
                        "then\n" +
                        "    return redis.call(\"del\",KEYS[1])\n" +
                        "else\n" +
                        "    return 0\n" +
                        "end";
                stringRedisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Arrays.asList("lock"), uuid);

            }
            return catalogJsonFromDB;
        } else {
            //加锁失败重试
            //休眠100ms
            System.out.println("分布式锁获取失败，等待重试");
            TimeUnit.MILLISECONDS.sleep(100);
            return getCatalogJsonFromDBWithRedisLock();
        }

    }

    public Map<String, List<Catalog2Vo>> getCatalogJsonFromDB() {
        //得到锁以后，我们应该再去缓存中确定一次，如果没有才需要继续查询
        String catalogJson = stringRedisTemplate.opsForValue().get("catalogJson");
        if (!StringUtils.isEmpty(catalogJson)) {
            //缓存不为空直接返回
            Map<String, List<Catalog2Vo>> result = JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catalog2Vo>>>() {
            });

            return result;
        }
        /**
         * 将数据库的多次查询变为一次
         */
        System.out.println("查询数据库");
        List<CategoryEntity> selectList = baseMapper.selectList(null);
        //1、查出所有一级分类
        List<CategoryEntity> level1Category = getParent_cid(selectList, 0L);
        Map<String, List<Catalog2Vo>> result = level1Category.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //遍历每一个一级分类，查找二级分类
            List<CategoryEntity> entities = getParent_cid(selectList, v.getCatId());
            List<Catalog2Vo> collect = null;
            if (entities != null && entities.size() != 0) {
                collect = entities.stream().map(item -> {
                    List<CategoryEntity> level3 = getParent_cid(selectList, item.getCatId());
                    //查找三级分类
                    List<Catalog2Vo.Catalog3Vo> collectLevel3 = level3.stream().map(l3 -> {
                        Catalog2Vo.Catalog3Vo catalog3Vo = new Catalog2Vo.Catalog3Vo(item.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                        return catalog3Vo;
                    }).collect(Collectors.toList());

                    Catalog2Vo catalog2Vo = new Catalog2Vo(v.getCatId().toString(), collectLevel3, item.getCatId().toString(), item.getName());
                    return catalog2Vo;
                }).collect(Collectors.toList());
            }
            return collect;
        }));
        String s = JSON.toJSONString(result);
        stringRedisTemplate.opsForValue().set("catalogJson", s, 1, TimeUnit.DAYS);

        return result;
    }


    private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList, Long parent_cid) {

        List<CategoryEntity> collect = selectList.stream().filter(item -> item.getParentCid() == parent_cid).collect(Collectors.toList());
        return collect;
//        return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));
    }

    private List<Long> findParentPath(Long catelogId, List<Long> path) {
        path.add(catelogId);
        CategoryEntity category = this.getById(catelogId);
        if (category.getParentCid() != 0) {
            List<Long> parentPath = findParentPath(category.getParentCid(), path);
        }
        return path;
    }

    /**
     * 递归查找某个菜单的子菜单
     *
     * @param root
     * @param all
     * @return
     */
    private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> all) {
        List<CategoryEntity> children = all.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid() == root.getCatId();
        }).map(categoryEntity -> {
            //递归继续寻找下一级菜单
            categoryEntity.setChildren(getChildren(categoryEntity, all));
            return categoryEntity;
        }).sorted((menu1, menu2) -> {
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());

        return children;
    }
}