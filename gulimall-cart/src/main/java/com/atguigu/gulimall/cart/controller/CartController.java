package com.atguigu.gulimall.cart.controller;

import com.atguigu.gulimall.cart.interceptor.CartInterceptor;
import com.atguigu.gulimall.cart.service.CartService;
import com.atguigu.gulimall.cart.vo.UserInfoTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author kLjSumi
 * @Date 2021/1/10
 */
@Controller
public class CartController {

    @GetMapping("/cart.html")
    public String cartListPage() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        System.out.println(userInfoTo.toString());
        return "cartList";
    }

    @GetMapping("/addToCart")
    public String add(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num) {
        return "success";
    }
}
