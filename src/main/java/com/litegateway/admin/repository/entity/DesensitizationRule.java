package com.litegateway.admin.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName(value = "desensitization_rule", autoResultMap = true)
public class DesensitizationRule {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String ruleId;

    private String ruleName;

    private String description;

    private String dataType;

    private String fieldPattern;

    private String desensitizationType;

    private Integer keepPrefix;

    private Integer keepSuffix;

    private String replacementChar;

    private String customRegex;

    private String customReplacement;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> routeIds;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> serviceIds;

    private Integer applyRequest;

    private Integer applyResponse;

    private Integer priority;

    private Integer enabled;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String createBy;

    private String updateBy;
}
