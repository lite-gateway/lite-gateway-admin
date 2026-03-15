package com.litegateway.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * AI Agent实体
 */
@Data
@TableName("ai_agent")
public class AiAgentEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * Agent唯一标识
     */
    private String agentId;

    /**
     * Agent名称
     */
    private String agentName;

    /**
     * 类型: personal/team/service
     */
    private String agentType;

    /**
     * 所属用户ID
     */
    private String ownerId;

    /**
     * 所属团队ID
     */
    private String teamId;

    /**
     * 每日Token配额
     */
    private Long dailyTokenQuota;

    /**
     * 每日成本配额
     */
    private BigDecimal dailyCostQuota;

    /**
     * 每月成本配额
     */
    private BigDecimal monthlyCostQuota;

    /**
     * 允许的模型列表(JSON)
     */
    private String allowedModels;

    /**
     * 允许的提供商列表(JSON)
     */
    private String allowedProviders;

    /**
     * 每秒请求限制
     */
    private Integer rateLimitRps;

    /**
     * 每分钟请求限制
     */
    private Integer rateLimitRpm;

    /**
     * 状态
     */
    private Integer status;

    /**
     * API Key哈希
     */
    private String apiKeyHash;

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
