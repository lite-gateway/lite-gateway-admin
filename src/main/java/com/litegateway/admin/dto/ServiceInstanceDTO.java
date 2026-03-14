package com.litegateway.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 服务实例 DTO
 */
@Data
@Schema(description = "服务实例数据传输对象")
public class ServiceInstanceDTO {

    @Schema(description = "实例ID")
    private Long id;

    @Schema(description = "关联服务ID")
    private Long serviceId;

    @Schema(description = "服务名")
    private String serviceName;

    @Schema(description = "实例ID（Nacos实例唯一标识）")
    private String instanceId;

    @Schema(description = "IP地址")
    private String ip;

    @Schema(description = "端口")
    private Integer port;

    @Schema(description = "权重")
    private Double weight;

    @Schema(description = "是否健康")
    private Boolean healthy;

    @Schema(description = "是否启用")
    private Boolean enabled;

    @Schema(description = "是否临时实例")
    private Boolean ephemeral;

    @Schema(description = "集群名")
    private String clusterName;

    @Schema(description = "实例元数据")
    private String metadata;

    @Schema(description = "最后心跳时间")
    private String lastBeatTime;
}
