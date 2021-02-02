package com.atguigu.gulimall.cart.vo;

import lombok.Data;

/**
 * @author kLjSumi
 * @Date 2021/1/10
 */
@Data
public class UserInfoTo {
    private Long userId;
    private String userKey;
    private boolean tempUser = false;
}
