package com.atguigu.gulimall.member.exception;

/**
 * @author kLjSumi
 * @Date 2020/12/28
 */
public class UserNameExistException extends RuntimeException {
    public UserNameExistException() {
        super("用户名存在");
    }
}
