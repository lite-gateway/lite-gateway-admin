package com.litegateway.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.litegateway.admin.common.web.PageBody;
import com.litegateway.admin.query.LogQuery;
import com.litegateway.admin.repository.entity.AccessLog;
import com.litegateway.admin.repository.mapper.AccessLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 日志服务
 */
@Slf4j
@Service
public class LogService {

    @Autowired
    private AccessLogMapper accessLogMapper;

    /**
     * 分页查询日志
     */
    public PageBody<AccessLog> selectLogPage(LogQuery query) {
        Page<AccessLog> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<AccessLog> wrapper = new LambdaQueryWrapper<>();

        // 动态条件
        if (StringUtils.isNotBlank(query.getRouteId())) {
            wrapper.eq(AccessLog::getRouteId, query.getRouteId());
        }
        if (StringUtils.isNotBlank(query.getPath())) {
            wrapper.like(AccessLog::getPath, query.getPath());
        }
        if (StringUtils.isNotBlank(query.getMethod())) {
            wrapper.eq(AccessLog::getMethod, query.getMethod());
        }
        if (StringUtils.isNotBlank(query.getClientIp())) {
            wrapper.eq(AccessLog::getClientIp, query.getClientIp());
        }
        if (query.getStatusCode() != null) {
            wrapper.eq(AccessLog::getStatusCode, query.getStatusCode());
        }
        if (StringUtils.isNotBlank(query.getUsername())) {
            wrapper.like(AccessLog::getUsername, query.getUsername());
        }
        if (StringUtils.isNotBlank(query.getStartTime())) {
            LocalDateTime start = parseDateTime(query.getStartTime());
            if (start != null) {
                wrapper.ge(AccessLog::getRequestTime, start);
            }
        }
        if (StringUtils.isNotBlank(query.getEndTime())) {
            LocalDateTime end = parseDateTime(query.getEndTime());
            if (end != null) {
                wrapper.le(AccessLog::getRequestTime, end);
            }
        }

        // 按请求时间倒序
        wrapper.orderByDesc(AccessLog::getRequestTime);

        Page<AccessLog> result = accessLogMapper.selectPage(page, wrapper);

        PageBody<AccessLog> pageBody = new PageBody<>();
        pageBody.setTotal(result.getTotal());
        pageBody.setPages((int) result.getPages());
        pageBody.setPageSize((int) result.getSize());
        pageBody.setPageNum((int) result.getCurrent());
        pageBody.setList(result.getRecords());

        return pageBody;
    }

    /**
     * 获取日志统计信息
     */
    public Map<String, Object> getLogStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        // 总请求数
        Long totalRequests = accessLogMapper.selectCount(null);
        statistics.put("totalRequests", totalRequests);

        // 今日请求数
        Long todayRequests = accessLogMapper.countTodayRequests();
        statistics.put("todayRequests", todayRequests);

        // 平均响应时间
        statistics.put("avgResponseTime", calculateAvgResponseTime());

        // 错误请求数 (状态码 >= 400)
        Long errorCount = getErrorCount();
        statistics.put("errorCount", errorCount);

        // 错误率
        if (totalRequests > 0) {
            statistics.put("errorRate", String.format("%.2f%%", (errorCount * 100.0) / totalRequests));
        } else {
            statistics.put("errorRate", "0.00%");
        }

        return statistics;
    }

    /**
     * 获取路由访问排行
     */
    public List<Map<String, Object>> getRouteRanking() {
        return accessLogMapper.selectRouteRanking();
    }

    /**
     * 获取状态码分布
     */
    public List<Map<String, Object>> getStatusDistribution() {
        return accessLogMapper.selectStatusDistribution();
    }

    /**
     * 获取响应时间趋势
     */
    public List<Map<String, Object>> getResponseTimeTrend(int hours) {
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusHours(hours);
        return accessLogMapper.selectResponseTimeTrend(startTime, endTime);
    }

    /**
     * 清理日志
     */
    public int cleanLogs(int days) {
        LocalDateTime beforeTime = LocalDateTime.now().minusDays(days);
        LambdaQueryWrapper<AccessLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.lt(AccessLog::getCreateTime, beforeTime);
        return accessLogMapper.delete(wrapper);
    }

    /**
     * 计算平均响应时间
     */
    private Long calculateAvgResponseTime() {
        return accessLogMapper.selectAvgResponseTime();
    }

    /**
     * 获取错误请求数
     */
    private Long getErrorCount() {
        LambdaQueryWrapper<AccessLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(AccessLog::getStatusCode, 400);
        return accessLogMapper.selectCount(wrapper);
    }

    /**
     * 解析日期时间字符串
     */
    private LocalDateTime parseDateTime(String dateTimeStr) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return LocalDateTime.parse(dateTimeStr, formatter);
        } catch (Exception e) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                return LocalDateTime.parse(dateTimeStr + " 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            } catch (Exception ex) {
                log.warn("Failed to parse datetime: {}", dateTimeStr);
                return null;
            }
        }
    }
}
