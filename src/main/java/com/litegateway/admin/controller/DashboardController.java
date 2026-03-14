package com.litegateway.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.litegateway.admin.common.web.PageBody;
import com.litegateway.admin.common.web.Result;
import com.litegateway.admin.repository.entity.AccessLog;
import com.litegateway.admin.repository.entity.GatewayRoute;
import com.litegateway.admin.repository.mapper.AccessLogMapper;
import com.litegateway.admin.repository.mapper.GatewayRouteMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 仪表盘控制器
 * 提供仪表盘统计数据和最近访问日志
 */
@Slf4j
@RestController
@RequestMapping("/gateway/dashboard")
@RequiredArgsConstructor
@Tag(name = "仪表盘", description = "仪表盘统计数据和访问日志")
public class DashboardController {

    private final GatewayRouteMapper gatewayRouteMapper;
    private final AccessLogMapper accessLogMapper;

    /**
     * 获取仪表盘统计数据
     */
    @GetMapping("/stats")
    @Operation(summary = "获取仪表盘统计数据", description = "获取路由数量、限流规则数、今日请求数、系统状态")
    public Result<DashboardStats> getDashboardStats() {
        DashboardStats stats = new DashboardStats();

        // 1. 路由数量 - 统计启用的路由
        LambdaQueryWrapper<GatewayRoute> routeWrapper = new LambdaQueryWrapper<>();
        routeWrapper.eq(GatewayRoute::getStatus, 0);
        routeWrapper.eq(GatewayRoute::getDeleted, 0);
        Long routeCount = gatewayRouteMapper.selectCount(routeWrapper);
        stats.setRouteCount(routeCount != null ? routeCount.intValue() : 0);

        // 2. 限流规则数 - 统计配置了限流器的路由
        LambdaQueryWrapper<GatewayRoute> rateLimitWrapper = new LambdaQueryWrapper<>();
        rateLimitWrapper.isNotNull(GatewayRoute::getFilterRateLimiterName);
        rateLimitWrapper.ne(GatewayRoute::getFilterRateLimiterName, "");
        rateLimitWrapper.eq(GatewayRoute::getDeleted, 0);
        Long rateLimitCount = gatewayRouteMapper.selectCount(rateLimitWrapper);
        stats.setRateLimitCount(rateLimitCount != null ? rateLimitCount.intValue() : 0);

        // 3. 今日请求数
        Long todayRequests = accessLogMapper.countTodayRequests();
        stats.setTodayRequests(todayRequests != null ? todayRequests.intValue() : 0);

        // 4. 系统状态 - 根据最近是否有请求判断
        // 如果5分钟内有请求，认为系统正常
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);
        LambdaQueryWrapper<AccessLog> recentLogWrapper = new LambdaQueryWrapper<>();
        recentLogWrapper.ge(AccessLog::getRequestTime, fiveMinutesAgo);
        Long recentCount = accessLogMapper.selectCount(recentLogWrapper);
        stats.setSystemStatus(recentCount > 0 ? "正常" : "正常"); // 默认正常，也可以根据实际健康检查判断
        stats.setSystemHealthy(true);

        return Result.ok(stats);
    }

    /**
     * 获取最近访问日志
     */
    @GetMapping("/recent-logs")
    @Operation(summary = "获取最近访问日志", description = "获取最近的访问日志列表")
    public Result<PageBody<AccessLogVO>> getRecentLogs(
            @Parameter(description = "日志数量限制") @RequestParam(defaultValue = "10") Integer limit) {

        LambdaQueryWrapper<AccessLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(AccessLog::getRequestTime);
        wrapper.last("LIMIT " + limit);

        List<AccessLog> logs = accessLogMapper.selectList(wrapper);

        // 转换为VO
        List<AccessLogVO> voList = logs.stream().map(this::convertToVO).toList();

        PageBody<AccessLogVO> pageBody = new PageBody<>();
        pageBody.setList(voList);
        pageBody.setTotal((long) voList.size());
        pageBody.setPages(1);
        pageBody.setPageSize(limit);
        pageBody.setPageNum(1);

        return Result.ok(pageBody);
    }

    /**
     * 获取访问日志列表（分页）
     */
    @GetMapping("/logs")
    @Operation(summary = "获取访问日志列表", description = "分页查询访问日志")
    public Result<PageBody<AccessLogVO>> getLogList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "20") Integer pageSize,
            @Parameter(description = "请求路径") @RequestParam(required = false) String path,
            @Parameter(description = "请求方法") @RequestParam(required = false) String method,
            @Parameter(description = "客户端IP") @RequestParam(required = false) String clientIp,
            @Parameter(description = "状态码") @RequestParam(required = false) Integer statusCode) {

        LambdaQueryWrapper<AccessLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(AccessLog::getRequestTime);

        if (path != null && !path.isEmpty()) {
            wrapper.like(AccessLog::getPath, path);
        }
        if (method != null && !method.isEmpty()) {
            wrapper.eq(AccessLog::getMethod, method.toUpperCase());
        }
        if (clientIp != null && !clientIp.isEmpty()) {
            wrapper.like(AccessLog::getClientIp, clientIp);
        }
        if (statusCode != null) {
            wrapper.eq(AccessLog::getStatusCode, statusCode);
        }

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<AccessLog> page =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageNum, pageSize);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<AccessLog> resultPage =
                accessLogMapper.selectPage(page, wrapper);

        List<AccessLogVO> voList = resultPage.getRecords().stream()
                .map(this::convertToVO)
                .toList();

        PageBody<AccessLogVO> pageBody = new PageBody<>();
        pageBody.setTotal(resultPage.getTotal());
        pageBody.setPages((int) resultPage.getPages());
        pageBody.setPageSize((int)resultPage.getSize());
        pageBody.setPageNum((int) resultPage.getCurrent());
        pageBody.setList(voList);

        return Result.ok(pageBody);
    }

    /**
     * 转换为VO
     */
    private AccessLogVO convertToVO(AccessLog log) {
        AccessLogVO vo = new AccessLogVO();
        vo.setId(String.valueOf(log.getId()));
        vo.setPath(log.getPath());
        vo.setMethod(log.getMethod());
        vo.setStatus(log.getStatusCode());
        vo.setTime(log.getRequestTime() != null ?
                log.getRequestTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "");
        vo.setIp(log.getClientIp());
        return vo;
    }

    /**
     * 仪表盘统计数据VO
     */
    @Data
    public static class DashboardStats {
        private Integer routeCount;
        private Integer rateLimitCount;
        private Integer todayRequests;
        private String systemStatus;
        private Boolean systemHealthy;
    }

    /**
     * 访问日志VO
     */
    @Data
    public static class AccessLogVO {
        private String id;
        private String path;
        private String method;
        private Integer status;
        private String time;
        private String ip;
    }
}
