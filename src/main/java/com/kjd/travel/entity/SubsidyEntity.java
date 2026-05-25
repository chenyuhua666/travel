package com.kjd.travel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("fk_reim_subsidy")
public class SubsidyEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long mainId;
    private Long tripId;
    private Integer subsidyDays;
    private BigDecimal applyAmount;
    private BigDecimal subsidyAmount;
    private BigDecimal mealAmount;
    private BigDecimal transportationAmount;
    private BigDecimal phoneAmount;
}
