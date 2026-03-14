package com.litegateway.admin.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 网关功能配置实体类
 * 用于管理网关各种功能的开关和配置
 */
@Data
@TableName("gateway_feature_config")
public class GatewayFeatureConfig {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 功能编码: sensitive_data_masking, circuit_breaker, auth_jwt 等
     */
    private String featureCode;

    /**
     * 功能名称
     */
    private String featureName;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 功能配置JSON
     */
    private String configJson;

    /**
     * 执行优先级
     */
    private Integer priority;

    /**
     * 适用的路由模式，逗号分隔支持通配符
     */
    private String routePatterns;

    /**
     * 描述
     */
    private String description;

    private String createBy;

    private LocalDateTime createTime;

    private String updateBy;

    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
