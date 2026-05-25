package com.kjd.travel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("biz_business_type")
public class BusinessTypeEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String businessTypeNo;
    private String businessTypeName;
    private Long parentId;
    private Integer leafFlag;
}
