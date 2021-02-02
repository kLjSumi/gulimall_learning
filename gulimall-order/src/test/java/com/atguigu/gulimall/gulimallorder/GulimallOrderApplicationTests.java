package com.atguigu.gulimall.gulimallorder;

import com.atguigu.gulimall.order.entity.OrderReturnReasonEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@Slf4j
@SpringBootTest
class GulimallOrderApplicationTests {

	@Autowired
	AmqpAdmin amqpAdmin;

	@Autowired
	RabbitTemplate rabbitTemplate;

	@Test
	void createExchange() {
		DirectExchange directExchange = new DirectExchange("hello-java-exchange",true,false);
		amqpAdmin.declareExchange(directExchange);
		log.info("Exchange [{}] 创建成功", directExchange.getName());
	}

	@Test
	void createQueue() {
		Queue queue = new Queue("hello-java-queue",true,false,false);
		amqpAdmin.declareQueue(queue);
		log.info("Queue [{}] 创建成功", queue.getName());
	}

	@Test
	void createBinding() {
		Binding binding = new Binding("hello-java-queue", Binding.DestinationType.QUEUE,"hello-java-exchange", "hello.java",null);
		amqpAdmin.declareBinding(binding);
		log.info("Binding [{}] 创建成功", "hello-java-binding");
	}

	@Test
	void sendMsg() {
		String hello_world = "Hello World";
		OrderReturnReasonEntity entity = new OrderReturnReasonEntity();
		entity.setId(1L);
		entity.setCreateTime(new Date());
		entity.setName("haha");
		//1、发送消息，如果发送的是个对象，我们会使用序列化机制，将对象写出去，对象必须实现Serializable
		//2、发送的对象类型的消息，以Json格式发出
		rabbitTemplate.convertAndSend("hello-java-exchange","hello.java", entity);
		log.info("消息发送完成 {}", entity);
	}

}
