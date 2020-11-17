package com.atguigu.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author kLjSumi
 * @Date 2020/11/17
 */
@Data
public class SpuBoundTo {
    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}
