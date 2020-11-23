package com.atguigu.gulimall.search;

import com.alibaba.fastjson.JSON;
import com.atguigu.gulimall.search.config.ElasticsearchConfig;
import lombok.Data;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;


@SpringBootTest
class GulimallSearchApplicationTests {

	@Resource
	private RestHighLevelClient restHighLevelClient;

	@Test
	void contextLoads() {
		System.out.println(restHighLevelClient);
	}

	/**
	 * 给es存储数据
	 */
	@Test
	void indexData() throws IOException {
		IndexRequest indexRequest = new IndexRequest("user");
		indexRequest.id("1");
//		indexRequest.source("username", "zhangsan", "age", 18);
		User user = new User();
		String jsonString = JSON.toJSONString(user);
		indexRequest.source(jsonString, XContentType.JSON);

		IndexResponse index = restHighLevelClient.index(indexRequest, ElasticsearchConfig.COMMON_OPTIONS);

		System.out.println(index);
	}

	@Data
	class User {
		private String username;
		private String gender;
		private String age;
	}

	@Test
	void searchData() throws IOException {

		SearchRequest searchRequest = new SearchRequest();
		searchRequest.indices("bank");
		//指定DSL
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(QueryBuilders.matchQuery("address", "mill"));
		System.out.println(searchSourceBuilder.toString());
		searchRequest.source(searchSourceBuilder);

		SearchResponse search = restHighLevelClient.search(searchRequest, ElasticsearchConfig.COMMON_OPTIONS);

		System.out.println(search);

	}

}
