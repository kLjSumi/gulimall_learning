package com.atguigu.gulimall.search.vo;

import com.atguigu.common.to.es.SkuEsModel;
import lombok.Data;

import java.util.List;

/**
 * @author kLjSumi
 * @Date 2020/12/12
 */
@Data
public class SearchResult {
    /**
     * 查询到的所有商品信息
     */
    private List<SkuEsModel> products;

    /**
     * 当前页面
     */
    private Integer pageNum;

    /**
     * 总记录数
     */
    private Long total;
    private Integer totalPages; //总页码数
    private List<Integer> pageNavs;

    /**
     * 聚合信息
     */
    private List<BrandVo> brands;  //当前查询到的结果，所有涉及到的品牌
    private List<AttrVo> attrs;  //当前查询到的结果，所有涉及到的属性
    private List<CatalogVo> catalogs;

    //==============以上是返回给页面的数据=================

    @Data
    public static class BrandVo {
        private Long brandId;
        private String brandName;
        private String brandImg;
    }

    @Data
    public static class AttrVo {
        private Long attrId;
        private String attrName;
        private List<String> attrValue;
    }

    @Data
    public static class CatalogVo {
        private Long catalogId;
        private String catalogName;
    }
}
