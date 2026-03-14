package com.litegateway.admin.controller;

import com.litegateway.admin.common.web.PageBody;
import com.litegateway.admin.common.web.Result;
import com.litegateway.admin.query.LogQuery;
import com.litegateway.admin.repository.entity.AccessLog;
import com.litegateway.admin.service.LogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 日志管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/gateway/log")
@Tag(name = "日志管理", description = "网关日志管理相关接口")
public class LogController {

    @Autowired
    private LogService logService;

    /**
     * 分页查询网关日志
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询日志", description = "分页查询网关访问日志列表")
    public Result<PageBody<AccessLog>> selectLogPage(@Validated @ModelAttribute LogQuery query) {
        PageBody<AccessLog> result = logService.selectLogPage(query);
        return Result.ok(result);
    }

    /**
     * 获取日志统计信息
     */
    @GetMapping("/statistics")
    @Operation(summary = "获取日志统计", description = "获取网关日志统计信息")
    public Result<Map<String, Object>> getLogStatistics() {
        Map<String, Object> statistics = logService.getLogStatistics();
        return Result.ok(statistics);
    }

    /**
     * 获取路由访问排行
     */
    @GetMapping("/ranking")
    @Operation(summary = "路由访问排行", description = "获取路由访问次数排行榜")
    public Result<List<Map<String, Object>>> getRouteRanking() {
        List<Map<String, Object>> ranking = logService.getRouteRanking();
        return Result.ok(ranking);
    }

    /**
     * 获取状态码分布
     */
    @GetMapping("/status-distribution")
    @Operation(summary = "状态码分布", description = "获取HTTP状态码分布统计")
    public Result<List<Map<String, Object>>> getStatusDistribution() {
        List<Map<String, Object>> distribution = logService.getStatusDistribution();
        return Result.ok(distribution);
    }

    /**
     * 获取响应时间趋势
     */
    @GetMapping("/response-time-trend")
    @Operation(summary = "响应时间趋势", description = "获取响应时间趋势数据")
    public Result<List<Map<String, Object>>> getResponseTimeTrend(
            @RequestParam(name = "hours", required = false, defaultValue = "24") int hours) {
        List<Map<String, Object>> trend = logService.getResponseTimeTrend(hours);
        return Result.ok(trend);
    }

    /**
     * 清理日志
     */
    @DeleteMapping("/clean")
    @Operation(summary = "清理日志", description = "清理指定天数前的日志数据")
    public Result<Void> cleanLogs(
            @RequestParam(name = "days", required = false, defaultValue = "7") int days) {
        int count = logService.cleanLogs(days);
        log.info("Cleaned {} logs older than {} days", count, days);
        return Result.ok();
    }
}
