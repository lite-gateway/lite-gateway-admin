package com.litegateway.admin.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.litegateway.admin.repository.entity.AccessLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;

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
}
