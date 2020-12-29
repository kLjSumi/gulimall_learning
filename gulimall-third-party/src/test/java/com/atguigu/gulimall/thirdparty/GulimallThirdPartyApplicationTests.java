package com.atguigu.gulimall.thirdparty;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import com.atguigu.gulimall.thirdparty.component.SmsComponent;
import com.atguigu.gulimall.thirdparty.util.HttpUtils;
import org.apache.http.HttpResponse;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class GulimallThirdPartyApplicationTests {

	@Autowired
	private SmsComponent smsComponent;

	@Test
	void contextLoads() {
		String endpoint = "http://oss-cn-shenzhen.aliyuncs.com";
// 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建RAM账号。
		String accessKeyId = "LTAI4G2ojCzb9bTC2CdDK1j8";
		String accessKeySecret = "r75KEHZNDykKSRABQvuBJFMMCGb0jg";

// 创建OSSClient实例。
		OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

// 创建PutObjectRequest对象。
		String content = "Hello OSS";
// <yourObjectName>表示上传文件到OSS时需要指定包含文件后缀在内的完整路径，例如abc/efg/123.jpg。
		PutObjectRequest putObjectRequest = new PutObjectRequest("gulimall-kljsumi", "a", new ByteArrayInputStream(content.getBytes()));

		ossClient.putObject(putObjectRequest);

// 关闭OSSClient。
		ossClient.shutdown();
	}

	@Test
	void sendMsg() {
		String host = "https://gyytz.market.alicloudapi.com";
		String path = "/sms/smsSend";
		String method = "POST";
		String appcode = "5a599b5132c4434a8dfe7cee11284720";
		Map<String, String> headers = new HashMap<String, String>();
		//最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
		headers.put("Authorization", "APPCODE " + appcode);
		Map<String, String> querys = new HashMap<String, String>();
		querys.put("mobile", "17742874629");
		querys.put("param", "");
		querys.put("smsSignId", "2e65b1bb3d054466b82f0c9d125465e2");
		querys.put("templateId", "f5e68c3ad6b6474faa8cd178b21d3377");
		Map<String, String> bodys = new HashMap<String, String>();


		try {
			HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
			System.out.println(response.toString());
			//获取response的body
			//System.out.println(EntityUtils.toString(response.getEntity()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	void sendMsgCode() {
		smsComponent.sendSmsCode("17742874629", "");
	}

}
