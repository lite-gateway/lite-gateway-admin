package com.litegateway.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 熔断规则 DTO
 */
@Data
@Schema(description = "熔断规则数据传输对象")
public class CircuitBreakerRuleDTO {

    @Schema(description = "规则ID")
    private String ruleId;

    @Schema(description = "规则名称")
    private String ruleName;

    @Schema(description = "关联路由ID")
    private String routeId;

    @Schema(description = "失败率阈值")
    private Float failureRateThreshold;

    @Schema(description = "熔断持续时间(秒)")
    private Integer waitDurationInOpenState;

    @Schema(description = "半开允许调用数")
    private Integer permittedNumberOfCallsInHalfOpenState;

    @Schema(description = "滑动窗口大小")
    private Integer slidingWindowSize;

    @Schema(description = "最小调用次数")
    private Integer minimumNumberOfCalls;

    @Schema(description = "慢调用率阈值")
    private Float slowCallRateThreshold;

    @Schema(description = "慢调用持续时间(秒)")
    private Integer slowCallDurationThreshold;

    @Schema(description = "超时时间(秒)")
    private Integer timeoutDuration;

    @Schema(description = "是否启用")
    private Boolean enabled;
}
