package com.atguigu.gulimall.product.service;

import com.atguigu.gulimall.product.vo.SpuSaveVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.SpuInfoEntity;

import java.util.Map;

/**
 * spu信息
 *
 * @author kLjSumi
 * @email 825963704@qq.com
 * @date 2020-10-30 16:40:39
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSpuInfo(SpuSaveVO vo);
}

