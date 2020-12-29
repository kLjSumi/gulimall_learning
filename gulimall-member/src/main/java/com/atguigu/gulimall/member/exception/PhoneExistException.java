package com.atguigu.gulimall.member.exception;

/**
 * @author kLjSumi
 * @Date 2020/12/28
 */
public class PhoneExistException extends RuntimeException {
    public PhoneExistException() {
        super("手机号存在");
    }
}
