package com.kjd.travel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_employee")
public class EmployeeEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String employeeNo;
    private String employeeName;
    private Long departmentId;
    private Long companyId;
}
