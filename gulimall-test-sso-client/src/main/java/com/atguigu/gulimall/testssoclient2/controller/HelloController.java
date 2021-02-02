package com.atguigu.gulimall.testssoclient2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * @author kLjSumi
 * @Date 2021/1/4
 */
@Controller
public class HelloController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Value("${sso.server.url}")
    private String ssoServerUrl;

    /**
     * 无须登录就可以访问
     * @return
     */
    @ResponseBody
    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    @GetMapping("/employees")
    public String employees(HttpSession session, Model model, @RequestParam(value = "token",required = false) String token) {

        if(!StringUtils.isEmpty(token)) {
            session.setAttribute("loginUser", "zhangsan");
        }


        Object loginUser = session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:" + ssoServerUrl + "?redirect_url=http://client1.com:8081/employees";
        } else {
            List<String> emps = new ArrayList<>();
            emps.add("张三");
            emps.add("李四");
            model.addAttribute("emps", emps);
            return "list";
        }
    }

}
