package com.kjd.travel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("biz_project")
public class ProjectEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String projectNo;
    private String projectName;
}
