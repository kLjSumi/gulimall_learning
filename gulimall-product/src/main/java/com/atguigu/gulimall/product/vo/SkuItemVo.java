package com.atguigu.gulimall.product.vo;

import com.atguigu.gulimall.product.entity.SkuImagesEntity;
import com.atguigu.gulimall.product.entity.SkuInfoEntity;
import com.atguigu.gulimall.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

/**
 * @author kLjSumi
 * @Date 2020/12/23
 */
@Data
public class SkuItemVo {
    //sku基本信息  pms_sku_info
    private SkuInfoEntity info;

    //sku的图片信息 pms_sku_images
    private List<SkuImagesEntity> images;

    //sku的销售属性
    private List<SkuItemSaleAttrVo> saleAttr;

    //获取spu的介绍
    private SpuInfoDescEntity desp;

    //获取spu的规格参数
    private List<SpuItemAttrGroupVo>  groupAttrs;

    boolean hasStock = true;
}
