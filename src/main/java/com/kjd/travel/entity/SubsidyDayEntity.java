package com.kjd.travel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@TableName("fk_reim_subsidy_day")
public class SubsidyDayEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long subsidyId;
    private LocalDate subsidyDate;
    private String weekdayName;
    private Long cityId;
    private String cityName;
    private BigDecimal mealStandardAmount;
    private BigDecimal transportationStandardAmount;
    private BigDecimal phoneStandardAmount;
    private Integer mealSelected;
    private Integer transportationSelected;
    private Integer phoneSelected;
    private BigDecimal mealAmount;
    private BigDecimal transportationAmount;
    private BigDecimal phoneAmount;
}
