package com.litegateway.admin.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 限流规则与路由关联实体类
 * 对应数据库表 rate_limit_route_relation
 *
 * 注意：此表已废弃，因为 GatewayRoute 现在直接通过 rate_limit_rule_id 字段关联限流规则
 * 保留此实体仅用于兼容旧数据，新功能请使用 GatewayRoute.rateLimitRuleId
 * @deprecated 使用 GatewayRoute.rateLimitRuleId 替代
 */
@Data
@TableName("rate_limit_route_relation")
@Deprecated
public class RateLimitRouteRelation {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 限流规则ID
     */
    private String rateLimitRuleId;

    /**
     * 路由ID
     */
    private String routeId;

    private LocalDateTime createTime;
}
