package com.atguigu.gulimall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @author kLjSumi
 * @Date 2020/11/19
 */
@Data
public class MergeVo {
    private Long purchaseId;
    private List<Long> items;
}
