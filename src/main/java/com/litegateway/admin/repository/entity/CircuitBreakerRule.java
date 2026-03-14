package com.litegateway.admin.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 熔断规则实体类
 * 用于配置路由级别的熔断降级策略
 */
@Data
@TableName("circuit_breaker_rule")
public class CircuitBreakerRule {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 规则唯一ID
     */
    private String ruleId;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 关联路由ID，空表示全局
     */
    private String routeId;

    /**
     * 失败率阈值(默认50)
     */
    private Float failureRateThreshold;

    /**
     * 熔断持续时间秒(默认60)
     */
    private Integer waitDurationInOpenState;

    /**
     * 半开允许调用数(默认10)
     */
    private Integer permittedNumberOfCallsInHalfOpenState;

    /**
     * 滑动窗口大小(默认100)
     */
    private Integer slidingWindowSize;

    /**
     * 最小调用次数(默认10)
     */
    private Integer minimumNumberOfCalls;

    /**
     * 慢调用率阈值(默认50)
     */
    private Float slowCallRateThreshold;

    /**
     * 慢调用持续时间秒(默认5)
     */
    private Integer slowCallDurationThreshold;

    /**
     * 超时时间秒(默认5)
     */
    private Integer timeoutDuration;

    /**
     * 是否启用
     */
    private Boolean enabled;

    private String createBy;

    private LocalDateTime createTime;

    private String updateBy;

    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
