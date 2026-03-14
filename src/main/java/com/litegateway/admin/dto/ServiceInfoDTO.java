package com.litegateway.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 服务信息 DTO
 */
@Data
@Schema(description = "服务信息数据传输对象")
public class ServiceInfoDTO {

    @Schema(description = "服务ID")
    private Long id;

    @Schema(description = "服务名（Nacos服务名）")
    private String serviceName;

    @Schema(description = "命名空间ID")
    private String namespaceId;

    @Schema(description = "分组名")
    private String groupName;

    @Schema(description = "显示名称")
    private String displayName;

    @Schema(description = "服务描述")
    private String description;

    @Schema(description = "服务协议：http/https/grpc")
    private String protocol;

    @Schema(description = "基础路径")
    private String basePath;

    @Schema(description = "Swagger文档地址")
    private String swaggerUrl;

    @Schema(description = "实例数量")
    private Integer instanceCount;

    @Schema(description = "健康实例数")
    private Integer healthyInstanceCount;

    @Schema(description = "服务状态：0-离线 1-在线 2-部分离线")
    private Integer status;

    @Schema(description = "是否从Nacos同步")
    private Boolean syncedFromNacos;

    @Schema(description = "最后同步时间")
    private String lastSyncTime;

    @Schema(description = "元数据")
    private String metadata;
}
