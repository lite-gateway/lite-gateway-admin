package com.litegateway.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 功能配置 DTO
 */
@Data
@Schema(description = "功能配置数据传输对象")
public class FeatureConfigDTO {

    @Schema(description = "功能编码")
    private String featureCode;

    @Schema(description = "功能名称")
    private String featureName;

    @Schema(description = "是否启用")
    private Boolean enabled;

    @Schema(description = "功能配置JSON")
    private String configJson;

    @Schema(description = "执行优先级")
    private Integer priority;

    @Schema(description = "适用路由模式")
    private String routePatterns;
}
