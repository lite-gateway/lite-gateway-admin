package com.litegateway.admin.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 健康检查配置实体类
 * 对应数据库表 health_check_config
 */
@Data
@TableName("health_check_config")
public class HealthCheckConfig {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String configId;
    private String configName;
    private Integer checkInterval;
    private Integer checkTimeout;
    private String checkPath;
    private String checkMethod;
    private Integer healthyThreshold;
    private Integer unhealthyThreshold;
    private Integer expectedStatusCode;
    private Integer status;
    private String description;

    @TableField(exist = false)
    private List<String> serviceIds;

    private String createBy;
    private LocalDateTime createTime;
    private String updateBy;
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
