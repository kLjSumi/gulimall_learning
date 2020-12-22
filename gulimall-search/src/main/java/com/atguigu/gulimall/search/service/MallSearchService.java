package com.atguigu.gulimall.search.service;

import com.atguigu.gulimall.search.vo.SearchParam;
import com.atguigu.gulimall.search.vo.SearchResult;

/**
 * @author kLjSumi
 * @Date 2020/12/12
 */
public interface MallSearchService {

    /**
     *
     * @param param  检索参数
     * @return 检索结果
     */
    SearchResult search(SearchParam param);
}
