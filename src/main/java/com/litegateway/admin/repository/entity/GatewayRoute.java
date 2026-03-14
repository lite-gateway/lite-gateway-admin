package com.litegateway.admin.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 网关路由实体类
 * 对应数据库表 gateway_route
 */
@Data
@TableName("gateway_route")
public class GatewayRoute {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String routeId;

    private String name;

    private String uri;

    private String path;

    private Integer stripPrefix;

    private String host;

    private String remoteAddr;

    private String header;

    /**
     * 限流规则ID，关联 rate_limit_rule 表
     */
    private String rateLimitRuleId;

    /**
     * 关联服务ID，对应 service_info 表
     */
    private Long serviceId;

    /**
     * 关联熔断规则ID，对应 circuit_breaker_rule 表
     */
    private String circuitBreakerRuleId;

    /**
     * 关联灰度规则ID，对应 canary_rule 表
     */
    private String canaryRuleId;

    private Integer weight;

    private String weightName;

    private Integer status;

    private String description;

    private String createBy;

    private LocalDateTime createTime;

    private String updateBy;

    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
