package com.atguigu.gulimall.ware.vo;

import lombok.Data;

/**
 * @author kLjSumi
 * @Date 2020/11/19
 */
@Data
public class PurchaseItemDoneVo {
    private Long itemId;
    private Integer status;
    private String reason;
}
