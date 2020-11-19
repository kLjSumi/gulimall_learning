package com.atguigu.gulimall.ware.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author kLjSumi
 * @Date 2020/11/19
 */
@FeignClient("gulimall-product")
public interface ProductFeignService {

    @GetMapping("/product/skuinfo/feigngetname")
    R getSkuNameById(@RequestParam("skuId") Long skuId);
}
