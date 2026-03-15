package com.litegateway.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI路由规则实体
 */
@Data
@TableName("ai_route_rule")
public class AiRouteRuleEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 规则编码
     */
    private String ruleCode;

    /**
     * 模型匹配模式
     */
    private String modelPattern;

    /**
     * 请求头匹配条件(JSON)
     */
    private String requestHeaders;

    /**
     * 请求参数匹配条件(JSON)
     */
    private String requestParams;

    /**
     * 路由策略类型
     */
    private String strategyType;

    /**
     * 目标提供商列表(JSON)
     */
    private String targetProviders;

    /**
     * 权重配置(JSON)
     */
    private String weights;

    /**
     * 条件表达式
     */
    private String conditions;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 状态
     */
    private Integer status;

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
