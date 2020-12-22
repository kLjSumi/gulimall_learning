package com.atguigu.gulimall.product.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author kLjSumi
 * @Date 2020/12/22
 */
@Controller
public class ItemController {

    /**
     * 展示当前sku的详情
     * @return
     */
    @GetMapping("/{skuId}.html")
    public String skuItem(Long skuId) {
        System.out.println("准备查询"+skuId+"详情");
        return "item";
    }
}
