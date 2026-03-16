package com.litegateway.admin.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@TableName(value = "rewrite_rule", autoResultMap = true)
public class RewriteRule {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String ruleId;

    private String ruleName;

    private String description;

    private String matchType;

    private String matchPattern;

    private String rewriteType;

    private String sourcePath;

    private String targetPath;

    private String sourceHost;

    private String targetHost;

    private String sourceMethod;

    private String targetMethod;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, String> headerRewrite;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, String> queryRewrite;

    private String bodyRewrite;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> routeIds;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> serviceIds;

    private Integer priority;

    private Integer enabled;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String createBy;

    private String updateBy;
}
