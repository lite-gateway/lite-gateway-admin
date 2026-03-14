package com.litegateway.admin.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.litegateway.admin.repository.entity.AccessLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 访问日志Mapper接口
 */
@Mapper
public interface AccessLogMapper extends BaseMapper<AccessLog> {

    /**
     * 统计今日请求数量
     */
    @Select("SELECT COUNT(*) FROM access_log WHERE DATE(request_time) = CURDATE()")
    Long countTodayRequests();

    /**
     * 统计指定时间范围内的请求数量
     */
    @Select("SELECT COUNT(*) FROM access_log WHERE request_time >= #{startTime} AND request_time <= #{endTime}")
    Long countRequestsByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 查询平均响应时间
     */
    @Select("SELECT AVG(duration) FROM access_log")
    Long selectAvgResponseTime();

    /**
     * 获取路由访问排行
     */
    @Select("SELECT route_id as routeId, COUNT(*) as count FROM access_log GROUP BY route_id ORDER BY count DESC LIMIT 10")
    List<Map<String, Object>> selectRouteRanking();

    /**
     * 获取状态码分布
     */
    @Select("SELECT status_code as statusCode, COUNT(*) as count FROM access_log GROUP BY status_code")
    List<Map<String, Object>> selectStatusDistribution();

    /**
     * 获取响应时间趋势（按小时统计）
     */
    @Select("SELECT DATE_FORMAT(request_time, '%Y-%m-%d %H:00:00') as time, AVG(duration) as avgDuration, COUNT(*) as count " +
            "FROM access_log WHERE request_time >= #{startTime} AND request_time <= #{endTime} " +
            "GROUP BY DATE_FORMAT(request_time, '%Y-%m-%d %H:00:00') ORDER BY time")
    List<Map<String, Object>> selectResponseTimeTrend(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}
