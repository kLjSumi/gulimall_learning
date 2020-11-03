package com.atguigu.gulimall.product.service.impl;

import org.springframework.stereotype.Service;

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


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

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