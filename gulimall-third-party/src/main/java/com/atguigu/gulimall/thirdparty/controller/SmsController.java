package com.atguigu.gulimall.thirdparty.controller;

import com.atguigu.common.utils.R;
import com.atguigu.gulimall.thirdparty.component.SmsComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author kLjSumi
 * @Date 2020/12/26
 */
@RestController
@RequestMapping("sms")
public class SmsController {

    @Autowired
    private SmsComponent smsComponent;

    /**
     * 提供给别的服务调用
     * @param phone
     * @param param
     * @return
     */
    @GetMapping("/sendcode")
    public R sendCode(@RequestParam("phone") String phone, @RequestParam("param") String param) {
        smsComponent.sendSmsCode(phone,param);
        return R.ok();
    }
}
