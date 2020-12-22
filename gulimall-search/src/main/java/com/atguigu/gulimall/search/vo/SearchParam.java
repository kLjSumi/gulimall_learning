package com.atguigu.gulimall.search.vo;

import lombok.Data;

import java.util.List;

/**
 * 封装页面所有可能传递过来的查询条件
 * catalog3Id=225&keyword=小米&sort=saleCount_asc&hasStock=1
 *
 * @author kLjSumi
 * @Date 2020/12/12
 */
@Data
public class SearchParam {
    /**
     * 查询条件
     */
    private String keyword; //全文匹配关键字
    private Long catalog3Id; //三级分类id
    private String sort; //需要排序的字段

    /**
     * 过滤条件
     */
    private Integer hasStock; //是否有货 0/1
    private String skuPrice;  //价格区间
    private List<Long> brandId; //品牌id
    private List<String> attrs; //按照属性

    /**
     * 分页数据
     */
    private Integer pageNum = 1; //页码

}
