package com.atguigu.gulimall.ware.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author kLjSumi
 * @Date 2020/11/19
 */
@Data
public class PurchaseDoneVo {
    @NotNull
    private Long id;  //采购单id
    private List<PurchaseItemDoneVo> items;
}
