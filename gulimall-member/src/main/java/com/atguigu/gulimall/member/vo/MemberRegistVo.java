package com.atguigu.gulimall.member.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

/**
 * @author kLjSumi
 * @Date 2020/12/27
 */
@Data
public class MemberRegistVo {
    private String userName;
    private String password;
    private String phone;
}
