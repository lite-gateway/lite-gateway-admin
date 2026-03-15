package com.litegateway.admin.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("trace_config")
public class TraceConfig {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String configId;
    private String configName;
    private Integer enabled;
    private String traceIdHeader;
    private String spanIdHeader;
    private String parentSpanIdHeader;
    private String sampledHeader;
    private String baggagePrefix;
    private Integer status;
    private String description;

    @TableField(exist = false)
    private List<String> routeIds;

    private String createBy;
    private LocalDateTime createTime;
    private String updateBy;
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
