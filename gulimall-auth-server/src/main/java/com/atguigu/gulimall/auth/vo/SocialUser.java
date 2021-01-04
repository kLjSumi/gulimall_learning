package com.atguigu.gulimall.auth.vo;

import lombok.Data;

/**
 * @author kLjSumi
 * @Date 2020/12/30
 *
 * {
 *     "access_token": "2.00y94ccG_5iktBfd202aba950ku43h",
 *     "remind_in": "157679999",
 *     "expires_in": 157679999,
 *     "uid": "6067444432",
 *     "isRealName": "true"
 * }
 */
@Data
public class SocialUser {
    private String access_token;
    private String remind_in;
    private String expires_in;
    private String uid;
    private String isRealName;
}
