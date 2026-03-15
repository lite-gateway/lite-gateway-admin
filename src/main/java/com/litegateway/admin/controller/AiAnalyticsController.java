package com.litegateway.admin.controller;

import com.litegateway.admin.mapper.AiCallLogMapper;
import com.litegateway.core.common.web.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI分析统计Controller
 */
@Tag(name = "AI分析统计", description = "AI网关的数据分析和统计")
@RestController
@RequestMapping("/api/ai/analytics")
@RequiredArgsConstructor
public class AiAnalyticsController {

    private final AiCallLogMapper callLogMapper;

    /**
     * 获取概览统计
     */
    @Operation(summary = "获取概览统计")
    @GetMapping("/overview")
    public Result<Map<String, Object>> getOverview(
            @Parameter(description = "开始日期") @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) LocalDate endDate) {
        
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(7);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = endDate.atTime(LocalTime.MAX);
        
        Map<String, Object> stats = callLogMapper.selectStatsByTimeRange(startTime, endTime);
        
        Map<String, Object> result = new HashMap<>();
        result.put("totalCalls", stats.get("callCount"));
        result.put("totalTokens", stats.get("totalTokens"));
        result.put("totalCost", stats.get("totalCost"));
        result.put("avgLatency", stats.get("avgLatency"));
        result.put("errorCount", stats.get("errorCount"));
        
        // 计算错误率
        long callCount = ((Number) stats.get("callCount")).longValue();
        long errorCount = ((Number) stats.get("errorCount")).longValue();
        double errorRate = callCount > 0 ? (double) errorCount / callCount * 100 : 0;
        result.put("errorRate", String.format("%.2f%%", errorRate));
        
        return Result.success(result);
    }

    /**
     * 按Agent统计
     */
    @Operation(summary = "按Agent统计")
    @GetMapping("/by-agent")
    public Result<List<Map<String, Object>>> getStatsByAgent(
            @Parameter(description = "开始日期") @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) LocalDate endDate) {
        
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(7);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = endDate.atTime(LocalTime.MAX);
        
        List<Map<String, Object>> list = callLogMapper.selectStatsByAgent(startTime, endTime);
        return Result.success(list);
    }

    /**
     * 按模型统计
     */
    @Operation(summary = "按模型统计")
    @GetMapping("/by-model")
    public Result<List<Map<String, Object>>> getStatsByModel(
            @Parameter(description = "开始日期") @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) LocalDate endDate) {
        
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(7);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = endDate.atTime(LocalTime.MAX);
        
        List<Map<String, Object>> list = callLogMapper.selectStatsByModel(startTime, endTime);
        return Result.success(list);
    }

    /**
     * 按提供商统计
     */
    @Operation(summary = "按提供商统计")
    @GetMapping("/by-provider")
    public Result<List<Map<String, Object>>> getStatsByProvider(
            @Parameter(description = "开始日期") @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) LocalDate endDate) {
        
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(7);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = endDate.atTime(LocalTime.MAX);
        
        List<Map<String, Object>> list = callLogMapper.selectStatsByProvider(startTime, endTime);
        return Result.success(list);
    }

    /**
     * 获取今日实时统计
     */
    @Operation(summary = "获取今日实时统计")
    @GetMapping("/today")
    public Result<Map<String, Object>> getTodayStats() {
        LocalDate today = LocalDate.now();
        LocalDateTime startTime = today.atStartOfDay();
        LocalDateTime endTime = today.atTime(LocalTime.MAX);
        
        Map<String, Object> stats = callLogMapper.selectStatsByTimeRange(startTime, endTime);
        
        Map<String, Object> result = new HashMap<>();
        result.put("todayCalls", stats.get("callCount"));
        result.put("todayTokens", stats.get("totalTokens"));
        result.put("todayCost", stats.get("totalCost"));
        result.put("todayInputTokens", stats.get("totalInputTokens"));
        result.put("todayOutputTokens", stats.get("totalOutputTokens"));
        
        return Result.success(result);
    }

    /**
     * 获取成本趋势
     */
    @Operation(summary = "获取成本趋势")
    @GetMapping("/cost-trend")
    public Result<List<Map<String, Object>>> getCostTrend(
            @Parameter(description = "天数") @RequestParam(defaultValue = "7") Integer days) {
        
        // TODO: 实现按天的成本趋势查询
        return Result.success(null);
    }
}
