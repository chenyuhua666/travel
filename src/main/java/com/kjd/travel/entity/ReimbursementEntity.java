package com.kjd.travel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("fk_reim_main")
public class ReimbursementEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String reimNo;
    private String reimbursementTitle;
    private Long reimburserId;
    private String reimburserNo;
    private String reimburserName;
    private Long reimDepartmentId;
    private String reimDepartmentNo;
    private String reimDepartmentName;
    private Long reimCompanyId;
    private String reimCompanyNo;
    private String reimCompanyName;
    private Long businessTypeId;
    private String businessTypeNo;
    private String businessTypeName;
    private String businessTripReason;
    private BigDecimal subsidyTotal;
    private BigDecimal mealAllowance;
    private BigDecimal transportationAllowance;
    private BigDecimal phoneAllowance;
    private String remarks;
    private Integer status;
    private Long ownerUserId;
    private LocalDateTime creationTime;
    private LocalDateTime updateTime;
}
