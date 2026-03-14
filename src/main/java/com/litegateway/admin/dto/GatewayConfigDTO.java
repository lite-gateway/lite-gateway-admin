package com.litegateway.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 网关配置 DTO
 * 供 Core 模块获取完整配置
 */
@Data
@Schema(description = "网关配置数据传输对象")
public class GatewayConfigDTO {

    @Schema(description = "配置版本号")
    private Long version;

    @Schema(description = "路由列表")
    private List<RouteDTO> routes;

    @Schema(description = "IP黑名单列表")
    private List<IpBlackDTO> ipBlacklist;

    @Schema(description = "白名单列表")
    private List<WhiteListDTO> whiteList;

    @Schema(description = "功能配置列表")
    private List<FeatureConfigDTO> featureConfigs;

    @Schema(description = "熔断规则列表")
    private List<CircuitBreakerRuleDTO> circuitBreakerRules;

    @Schema(description = "灰度规则列表")
    private List<CanaryRuleDTO> canaryRules;

    @Schema(description = "服务列表")
    private List<ServiceInfoDTO> services;
}
