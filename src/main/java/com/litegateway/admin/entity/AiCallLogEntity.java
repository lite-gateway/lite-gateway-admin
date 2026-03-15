package com.litegateway.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * AI调用日志实体
 */
@Data
@TableName("ai_call_log")
public class AiCallLogEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 请求ID
     */
    private String requestId;

    /**
     * 追踪ID
     */
    private String traceId;

    /**
     * Agent ID
     */
    private String agentId;

    /**
     * 提供商ID
     */
    private Long providerId;

    /**
     * 模型ID
     */
    private Long modelId;

    /**
     * 请求的模型
     */
    private String requestedModel;

    /**
     * 输入token数
     */
    private Integer inputTokens;

    /**
     * 输出token数
     */
    private Integer outputTokens;

    /**
     * 总token数
     */
    private Integer totalTokens;

    /**
     * 输入成本
     */
    private BigDecimal inputCost;

    /**
     * 输出成本
     */
    private BigDecimal outputCost;

    /**
     * 总成本
     */
    private BigDecimal totalCost;

    /**
     * 货币
     */
    private String currency;

    /**
     * 延迟(毫秒)
     */
    private Integer latencyMs;

    /**
     * 首token延迟
     */
    private Integer firstTokenLatencyMs;

    /**
     * 请求头(JSON)
     */
    private String requestHeaders;

    /**
     * 响应状态码
     */
    private Integer responseStatus;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 是否流式
     */
    private Integer isStreaming;

    /**
     * 请求时间
     */
    private LocalDateTime requestTime;

    /**
     * 响应时间
     */
    private LocalDateTime responseTime;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
