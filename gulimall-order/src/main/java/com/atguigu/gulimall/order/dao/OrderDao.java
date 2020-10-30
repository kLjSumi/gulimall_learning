package com.atguigu.gulimall.order.dao;

import com.atguigu.gulimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author kLjSumi
 * @email 825963704@qq.com
 * @date 2020-10-30 23:03:28
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
