package com.litegateway.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * API信息 DTO
 */
@Data
@Schema(description = "API信息数据传输对象")
public class ApiInfoDTO {

    @Schema(description = "API ID")
    private Long id;

    @Schema(description = "API路径")
    private String path;

    @Schema(description = "HTTP方法")
    private String method;

    @Schema(description = "所属服务ID")
    private Long serviceId;

    @Schema(description = "所属服务名")
    private String serviceName;

    @Schema(description = "API版本")
    private String version;

    @Schema(description = "API名称/标题")
    private String title;

    @Schema(description = "API描述")
    private String description;

    @Schema(description = "状态：0-草稿 1-已发布 2-已下线")
    private Integer status;

    @Schema(description = "是否需要认证")
    private Boolean requireAuth;

    @Schema(description = "请求参数描述")
    private String requestParams;

    @Schema(description = "响应参数描述")
    private String responseParams;

    @Schema(description = "关联的路由ID")
    private Long routeId;

    @Schema(description = "Swagger来源")
    private String swaggerSource;

    @Schema(description = "标签/分组")
    private String tags;
}
