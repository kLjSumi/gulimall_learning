package com.atguigu.gulimall.product.vo;

import lombok.Data;

import java.util.List;

/**
 * @author kLjSumi
 * @Date 2020/12/24
 */
@Data
public class SpuItemAttrGroupVo {
    private String groupName;
    private List<Attr> attrs;

}
