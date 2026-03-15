package com.litegateway.admin.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("health_check_service_relation")
public class HealthCheckServiceRelation {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String configId;
    private String serviceId;
    private LocalDateTime createTime;

    @TableLogic
    private Integer deleted;
}
