package com.kjd.travel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("biz_city")
public class CityEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String cityNo;
    private String cityName;
    private Integer cityType;
}
