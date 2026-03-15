package com.litegateway.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI提供商实体
 */
@Data
@TableName("ai_provider")
public class AiProviderEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 提供商编码
     */
    private String providerCode;

    /**
     * 提供商名称
     */
    private String providerName;

    /**
     * 类型: cloud/local/proxy
     */
    private String providerType;

    /**
     * 基础URL
     */
    private String baseUrl;

    /**
     * 加密的API Key
     */
    private String apiKeyEncrypted;

    /**
     * API版本
     */
    private String apiVersion;

    /**
     * 支持的能力(JSON)
     */
    private String capabilities;

    /**
     * 协议类型
     */
    private String protocolType;

    /**
     * 超时时间(毫秒)
     */
    private Integer timeoutMs;

    /**
     * 最大重试次数
     */
    private Integer maxRetries;

    /**
     * 并发限制
     */
    private Integer concurrentLimit;

    /**
     * 状态: 0-禁用 1-启用
     */
    private Integer status;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 权重
     */
    private Integer weight;

    /**
     * 是否启用健康检查
     */
    private Integer healthCheckEnabled;

    /**
     * 健康检查URL
     */
    private String healthCheckUrl;

    /**
     * 最后健康检查时间
     */
    private LocalDateTime lastHealthCheckTime;

    /**
     * 健康状态
     */
    private String healthStatus;

    /**
     * 描述
     */
    private String description;

    /**
     * 标签
     */
    private String tags;

    /**
     * 扩展配置(JSON)
     */
    private String extraConfig;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新人
     */
    private String updatedBy;

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
