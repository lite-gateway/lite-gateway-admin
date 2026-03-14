package com.litegateway.admin.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 限流规则实体类
 * 对应数据库表 rate_limit_rule
 */
@Data
@TableName("rate_limit_rule")
public class RateLimitRule {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 规则业务ID（唯一标识）
     */
    private String ruleId;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 限流算法类型：token-bucket(令牌桶)、sliding-window(滑动窗口)、fixed-window(固定窗口)
     */
    private String algorithm;

    /**
     * 限流类型：1-IP限流, 2-用户限流, 3-全局限流
     */
    private Integer limitType;

    /**
     * 填充速率（QPS）
     */
    private Integer replenishRate;

    /**
     * 令牌桶容量/突发容量
     */
    private Integer burstCapacity;

    /**
     * 每次请求消耗令牌数
     */
    private Integer requestedTokens;

    /**
     * 预热时长（毫秒）
     */
    private Integer warmupPeriod;

    /**
     * 响应头自定义（如X-RateLimit-Remaining）
     */
    private String responseHeader;

    /**
     * 路径匹配模式，逗号分隔（如 /api/order/**,/api/pay/**）
     */
    private String pathPatterns;

    /**
     * 状态：1启用 0禁用
     */
    private Integer status;

    /**
     * 关联的路由ID列表（非数据库字段，多对多关系）
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
