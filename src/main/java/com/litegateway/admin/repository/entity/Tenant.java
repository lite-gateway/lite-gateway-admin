package com.litegateway.admin.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@TableName(value = "tenant", autoResultMap = true)
public class Tenant {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String tenantId;

    private String tenantName;

    private String tenantCode;

    private String description;

    private String contactName;

    private String contactEmail;

    private String contactPhone;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> routeIds;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> serviceIds;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, String> resourceQuota;

    private Integer rateLimit;

    private Long maxConnections;

    private String isolationLevel;

    private String status;

    private LocalDateTime expireTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String createBy;

    private String updateBy;
}
