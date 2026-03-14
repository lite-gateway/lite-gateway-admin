package com.litegateway.admin.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 日志查询参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "日志查询参数")
public class LogQuery extends PageQuery {

    @Schema(description = "路由ID")
    private String routeId;

    @Schema(description = "请求路径")
    private String path;

    @Schema(description = "请求方法")
    private String method;

    @Schema(description = "客户端IP")
    private String clientIp;

    @Schema(description = "响应状态码")
    private Integer statusCode;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "开始时间")
    private String startTime;

    @Schema(description = "结束时间")
    private String endTime;
}
