package com.litegateway.admin.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 限流规则与路由关联实体类
 * 对应数据库表 rate_limit_route_relation
 * 用于实现限流规则与路由的多对多关系
 */
@Data
@TableName("rate_limit_route_relation")
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
