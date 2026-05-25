package com.kjd.travel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("fk_reim_allocation")
public class AllocationEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long mainId;
    private Long companyId;
    private String companyNo;
    private String companyName;
    private Long projectId;
    private String projectNo;
    private String projectName;
    private BigDecimal allocationRatio;
    private BigDecimal allocationAmount;
    private Integer rowOrder;
}
