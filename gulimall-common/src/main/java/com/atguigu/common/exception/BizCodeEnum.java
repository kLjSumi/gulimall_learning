package com.atguigu.common.exception;

/**
 * @author kLjSumi
 * @Date 2020/11/4
 *
 *
 * 错误码列表
 * 10：通用
 *      001：参数格式校验
 * 11：商品
 * 12：订单
 * 13：购物车
 * 14：物流
 * 15：用户
 */
public enum BizCodeEnum {
    UNKNOWN_EXCEPTION(10000,"系统未知异常"),
    VALID_EXCEPTION(10001,"参数格式校验失败"),
    SMS_CODE_EXCEPTION(10002,"获取验证码频率太高，稍后再试"),

    PRODUCT_UP_EXCEPTION(11000,"商品上架异常"),

    USER_EXIST_EXCEPTION(15001,"用户存在"),
    PHONE_EXIST_EXCEPTION(15002,"手机号存在"),
    LOGINACCT_PASSWORD_INVALID_EXCEPTION(15003,"账号或密码错误")
    ;

    private int code;
    private String message;

    BizCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }


    public String getMessage() {
        return message;
    }
}
