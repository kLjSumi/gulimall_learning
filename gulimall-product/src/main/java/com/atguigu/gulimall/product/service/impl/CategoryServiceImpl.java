package com.atguigu.gulimall.product.service.impl;

import com.atguigu.gulimall.product.service.CategoryBrandRelationService;
import com.atguigu.gulimall.product.vo.Catalog2Vo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
        ).map((menu)-> {
            menu.setChildren(getChildren(menu, entities));
            return menu;
        }).sorted((menu1, menu2)-> {
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
     * @param category
     */
    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        if (!StringUtils.isEmpty(category.getName())) {
            categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
        }
    }

    @Override
    public List<CategoryEntity> getLevel1Category() {

        List<CategoryEntity> entities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        return entities;
    }

    @Override
    public Map<String, List<Catalog2Vo>> getCatalogJson() {

        /**
         * 将数据库的多次查询变为一次
         */
        List<CategoryEntity> entities1 = baseMapper.selectList(null);
        //1、查出所有一级分类
        List<CategoryEntity> level1Category = getLevel1Category();
        Map<String, List<Catalog2Vo>> result = level1Category.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //遍历每一个一级分类，查找二级分类
            List<CategoryEntity> entities = getParent_cid(v);
            List<Catalog2Vo> collect = null;
            if (entities != null && entities.size() != 0) {
                collect = entities.stream().map(item -> {
                    List<CategoryEntity> level3 = getParent_cid(item);
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
        return result;
    }

    //TODO 完善该方法
    private List<CategoryEntity> getParent_cid(CategoryEntity v) {
        return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));
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
     * @param root
     * @param all
     * @return
     */
    private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> all) {
        List<CategoryEntity> children = all.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid() == root.getCatId();
        }).map(categoryEntity -> {
            //递归继续寻找下一级菜单
            categoryEntity.setChildren(getChildren(categoryEntity,all));
            return categoryEntity;
        }).sorted((menu1, menu2)-> {
            return (menu1.getSort()==null?0:menu1.getSort()) - (menu2.getSort()==null?0:menu2.getSort());
        }).collect(Collectors.toList());

        return children;
    }
}