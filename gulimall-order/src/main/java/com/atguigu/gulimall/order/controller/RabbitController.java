package com.atguigu.gulimall.order.controller;

import com.atguigu.gulimall.order.entity.OrderReturnReasonEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @author kLjSumi
 * @Date 2021/2/1
 */
@Slf4j
@RestController
public class RabbitController {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @GetMapping("/sendMsg")
    public String send() {
        String hello_world = "Hello World";
        OrderReturnReasonEntity entity = new OrderReturnReasonEntity();
        entity.setId(1L);
        entity.setCreateTime(new Date());
        entity.setName("haha");
        //1、发送消息，如果发送的是个对象，我们会使用序列化机制，将对象写出去，对象必须实现Serializable
        //2、发送的对象类型的消息，以Json格式发出
        rabbitTemplate.convertAndSend("hello-java-exchange","hello.java", entity);
        log.info("消息发送完成 {}", entity);

        return "ok";
    }
}
