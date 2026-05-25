package com.kjd.travel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_company")
public class CompanyEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String companyNo;
    private String companyName;
}
