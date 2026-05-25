package com.kjd.travel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;

@Data
@TableName("fk_reim_trip")
public class TripEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long mainId;
    private Long travelerId;
    private String travelerNo;
    private String travelerName;
    private Long departCityId;
    private String departCityNo;
    private String departCityName;
    private Long arriveCityId;
    private String arriveCityNo;
    private String arriveCityName;
    private LocalDate departDate;
    private LocalDate arriveDate;
    private String tripDescription;
}
