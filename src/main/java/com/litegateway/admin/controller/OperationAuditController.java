package com.litegateway.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.litegateway.admin.repository.entity.OperationAudit;
import com.litegateway.admin.repository.mapper.OperationAuditMapper;
import com.litegateway.admin.common.web.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * 操作审计日志Controller
 */
@Tag(name = "操作审计", description = "操作日志查询和统计")
@RestController
@RequestMapping("/gateway/audit")
@RequiredArgsConstructor
public class OperationAuditController {

    private final OperationAuditMapper auditMapper;

    /**
     * 分页查询审计日志
     */
    @Operation(summary = "分页查询审计日志")
    @GetMapping
    public Result<Page<OperationAudit>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "操作模块") @RequestParam(required = false) String module,
            @Parameter(description = "操作类型") @RequestParam(required = false) String operationType,
            @Parameter(description = "用户名") @RequestParam(required = false) String username,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        
        LambdaQueryWrapper<OperationAudit> wrapper = new LambdaQueryWrapper<OperationAudit>()
                .orderByDesc(OperationAudit::getCreatedAt);
        
        if (module != null && !module.isEmpty()) {
            wrapper.eq(OperationAudit::getModule, module);
        }
        
        if (operationType != null && !operationType.isEmpty()) {
            wrapper.eq(OperationAudit::getOperationType, operationType);
        }
        
        if (username != null && !username.isEmpty()) {
            wrapper.like(OperationAudit::getUsername, username);
        }
        
        if (startDate != null) {
            wrapper.ge(OperationAudit::getCreatedAt, startDate.atStartOfDay());
        }
        
        if (endDate != null) {
            wrapper.le(OperationAudit::getCreatedAt, endDate.atTime(LocalTime.MAX));
        }
        
        Page<OperationAudit> result = auditMapper.selectPage(new Page<>(page, size), wrapper);
        return Result.success(result);
    }

    /**
     * 获取审计日志详情
     */
    @Operation(summary = "获取审计日志详情")
    @GetMapping("/{id}")
    public Result<OperationAudit> getById(@PathVariable Long id) {
        OperationAudit audit = auditMapper.selectById(id);
        if (audit == null) {
            return Result.error("日志不存在");
        }
        return Result.success(audit);
    }

    /**
     * 获取操作类型统计
     */
    @Operation(summary = "获取操作类型统计")
    @GetMapping("/stats/operation-type")
    public Result<List<Map<String, Object>>> getOperationTypeStats(
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(7);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = endDate.atTime(LocalTime.MAX);
        
        List<Map<String, Object>> stats = auditMapper.selectOperationTypeStats(startTime, endTime);
        return Result.success(stats);
    }

    /**
     * 获取模块统计
     */
    @Operation(summary = "获取模块统计")
    @GetMapping("/stats/module")
    public Result<List<Map<String, Object>>> getModuleStats(
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(7);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = endDate.atTime(LocalTime.MAX);
        
        List<Map<String, Object>> stats = auditMapper.selectModuleStats(startTime, endTime);
        return Result.success(stats);
    }
}
