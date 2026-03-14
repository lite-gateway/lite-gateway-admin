package com.litegateway.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 灰度规则 DTO
 */
@Data
@Schema(description = "灰度规则数据传输对象")
public class CanaryRuleDTO {

    @Schema(description = "规则ID")
    private String ruleId;

    @Schema(description = "规则名称")
    private String ruleName;

    @Schema(description = "关联路由ID")
    private String routeId;

    @Schema(description = "金丝雀权重 0-100")
    private Integer canaryWeight;

    @Schema(description = "金丝雀版本")
    private String canaryVersion;

    @Schema(description = "稳定版本")
    private String stableVersion;

    @Schema(description = "匹配类型: weight | user | header | cookie | query")
    private String matchType;

    @Schema(description = "匹配配置JSON")
    private String matchConfig;

    @Schema(description = "Header名称")
    private String headerName;

    @Schema(description = "Header值")
    private String headerValue;

    @Schema(description = "Cookie名称")
    private String cookieName;

    @Schema(description = "Query参数名")
    private String queryParam;

    @Schema(description = "是否启用")
    private Boolean enabled;
}
