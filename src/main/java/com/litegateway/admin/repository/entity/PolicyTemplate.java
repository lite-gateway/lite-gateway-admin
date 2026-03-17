package com.litegateway.admin.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 策略模板实体
 */
@Data
@TableName("policy_template")
public class PolicyTemplate {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 模板编码
     */
    private String templateCode;

    /**
     * 策略类型：rate_limit-限流，circuit_breaker-熔断，retry-重试，
     *          canary-灰度，load_balance-负载均衡，timeout-超时
     */
    private String policyType;

    /**
     * 模板描述
     */
    private String description;

    /**
     * 策略配置(JSON)
     */
    private String policyConfig;

    /**
     * 适用场景
     */
    private String applicableScenarios;

    /**
     * 是否系统预设：0-否，1-是
     */
    private Integer isSystem;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 使用次数
     */
    private Long usageCount;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer deleted;
}
