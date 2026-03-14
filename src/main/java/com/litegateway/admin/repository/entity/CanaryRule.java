package com.litegateway.admin.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 灰度规则实体类
 * 用于配置金丝雀发布和灰度发布策略
 */
@Data
@TableName("canary_rule")
public class CanaryRule {

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
     * 关联路由ID
     */
    private String routeId;

    /**
     * 金丝雀权重 0-100
     */
    private Integer canaryWeight;

    /**
     * 金丝雀版本
     */
    private String canaryVersion;

    /**
     * 稳定版本
     */
    private String stableVersion;

    /**
     * 匹配类型: weight | user | header | cookie | query
     */
    private String matchType;

    /**
     * 匹配配置JSON
     */
    private String matchConfig;

    /**
     * Header名称(当matchType=header时)
     */
    private String headerName;

    /**
     * Header值
     */
    private String headerValue;

    /**
     * Cookie名称
     */
    private String cookieName;

    /**
     * Query参数名
     */
    private String queryParam;

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
