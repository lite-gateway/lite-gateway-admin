package com.litegateway.admin.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 超时配置实体类
 * 对应数据库表 timeout_config
 * 支持路由级别和全局级别的超时控制
 */
@Data
@TableName("timeout_config")
public class TimeoutConfig {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 配置业务ID（唯一标识）
     */
    private String configId;

    /**
     * 配置名称
     */
    private String configName;

    /**
     * 配置类型：global(全局)、route(路由级别)
     */
    private String configType;

    /**
     * 连接超时（毫秒）
     */
    private Integer connectTimeout;

    /**
     * 读取超时（毫秒）
     */
    private Integer readTimeout;

    /**
     * 写入超时（毫秒）
     */
    private Integer writeTimeout;

    /**
     * 响应超时（毫秒）
     */
    private Integer responseTimeout;

    /**
     * 是否启用熔断联动：0否 1是
     */
    private Integer circuitBreakerEnabled;

    /**
     * 熔断规则ID（关联 circuit_breaker_rule 表）
     */
    private String circuitBreakerRuleId;

    /**
     * 状态：1启用 0禁用
     */
    private Integer status;

    /**
     * 描述
     */
    private String description;

    /**
     * 关联的路由ID列表（非数据库字段，configType=route时使用）
     */
    @TableField(exist = false)
    private List<String> routeIds;

    private String createBy;

    private LocalDateTime createTime;

    private String updateBy;

    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
