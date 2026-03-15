package com.litegateway.admin.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("metrics_config")
public class MetricsConfig {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String configId;
    private String configName;
    private Integer prometheusEnabled;
    private String prometheusPath;
    private Integer grafanaEnabled;
    private String grafanaUrl;
    private Integer alertEnabled;
    private Integer status;
    private String description;

    private String createBy;
    private LocalDateTime createTime;
    private String updateBy;
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
