package com.litegateway.admin.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * WAF规则实体
 */
@Data
@TableName("waf_rule")
public class WafRule {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 规则类型：sql_injection-SQL注入，xss-XSS攻击，csrf-CSRF攻击，
     *          path_traversal-路径遍历，file_upload-文件上传，
     *          bot-恶意爬虫，custom-自定义
     */
    private String ruleType;

    /**
     * 匹配模式：regex-正则，keyword-关键字，path-路径，ip-IP地址
     */
    private String matchMode;

    /**
     * 匹配内容
     */
    private String matchContent;

    /**
     * 风险等级：low-低，medium-中，high-高，critical-严重
     */
    private String riskLevel;

    /**
     * 动作：block-拦截，log-记录，captcha-验证码，rate_limit-限流
     */
    private String action;

    /**
     * 拦截状态码
     */
    private Integer blockStatusCode;

    /**
     * 拦截响应内容
     */
    private String blockResponse;

    /**
     * 规则描述
     */
    private String description;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 命中次数
     */
    private Long hitCount;

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
