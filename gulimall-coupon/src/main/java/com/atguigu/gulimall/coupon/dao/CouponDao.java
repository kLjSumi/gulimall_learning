package com.atguigu.gulimall.coupon.dao;

import com.atguigu.gulimall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author kLjSumi
 * @email 825963704@qq.com
 * @date 2020-10-30 21:44:07
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
