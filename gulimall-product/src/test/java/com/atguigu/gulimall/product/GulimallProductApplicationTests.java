package com.atguigu.gulimall.product;

import com.aliyun.oss.OSSClient;
import com.atguigu.gulimall.product.entity.BrandEntity;
import com.atguigu.gulimall.product.service.BrandService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;

@SpringBootTest
class GulimallProductApplicationTests {

    @Autowired
    BrandService brandService;

    @Autowired
    OSSClient ossClient;

    @Test
    public void testUpload() {
        // Endpoint以杭州为例，其它Region请按实际情况填写。
//        String endpoint = "oss-cn-shenzhen.aliyuncs.com";
//        // 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录RAM控制台创建RAM账号。
//        String accessKeyId = "LTAI4G6sF9bLVPj6dD7X1AfL";
//        String accessKeySecret = "MJok4zr7y3J4AlhdstQlGnRZmSopj7";
        String bucketName = "gulimall-kljsumi";
//
        String objectName = "a/a.text";
//
//        // 创建OSSClient实例。
//        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
//
//        // 上传文件到指定的存储空间（bucketName）并将其保存为指定的文件名称（objectName）。
        String content = "Hello OSS";
        ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(content.getBytes()));

        // 关闭OSSClient。
        ossClient.shutdown();
    }

    @Test
    void contextLoads() {
        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setBrandId(1L);
        brandEntity.setDescript("帅");
//		brandService.save(brandEntity);
//		System.out.println("保存成功！！！");

        brandService.updateById(brandEntity);
    }

}
