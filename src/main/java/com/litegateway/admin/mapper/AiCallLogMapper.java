package com.litegateway.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.litegateway.admin.entity.AiCallLogEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * AI调用日志Mapper
 */
@Mapper
public interface AiCallLogMapper extends BaseMapper<AiCallLogEntity> {

    /**
     * 统计指定时间范围内的调用数据
     */
    @Select("SELECT " +
            "COUNT(*) as callCount, " +
            "SUM(input_tokens) as totalInputTokens, " +
            "SUM(output_tokens) as totalOutputTokens, " +
            "SUM(total_tokens) as totalTokens, " +
            "SUM(total_cost) as totalCost, " +
            "AVG(latency_ms) as avgLatency, " +
            "SUM(CASE WHEN response_status >= 400 THEN 1 ELSE 0 END) as errorCount " +
            "FROM ai_call_log " +
            "WHERE request_time BETWEEN #{startTime} AND #{endTime}")
    Map<String, Object> selectStatsByTimeRange(@Param("startTime") LocalDateTime startTime, 
                                                @Param("endTime") LocalDateTime endTime);

    /**
     * 按Agent统计
     */
    @Select("SELECT agent_id, " +
            "COUNT(*) as callCount, " +
            "SUM(total_cost) as totalCost, " +
            "SUM(total_tokens) as totalTokens " +
            "FROM ai_call_log " +
            "WHERE request_time BETWEEN #{startTime} AND #{endTime} " +
            "GROUP BY agent_id")
    List<Map<String, Object>> selectStatsByAgent(@Param("startTime") LocalDateTime startTime, 
                                                  @Param("endTime") LocalDateTime endTime);

    /**
     * 按模型统计
     */
    @Select("SELECT model_id, requested_model, " +
            "COUNT(*) as callCount, " +
            "SUM(total_cost) as totalCost, " +
            "SUM(total_tokens) as totalTokens " +
            "FROM ai_call_log " +
            "WHERE request_time BETWEEN #{startTime} AND #{endTime} " +
            "GROUP BY model_id, requested_model")
    List<Map<String, Object>> selectStatsByModel(@Param("startTime") LocalDateTime startTime, 
                                                  @Param("endTime") LocalDateTime endTime);

    /**
     * 按提供商统计
     */
    @Select("SELECT provider_id, " +
            "COUNT(*) as callCount, " +
            "SUM(total_cost) as totalCost, " +
            "SUM(total_tokens) as totalTokens " +
            "FROM ai_call_log " +
            "WHERE request_time BETWEEN #{startTime} AND #{endTime} " +
            "GROUP BY provider_id")
    List<Map<String, Object>> selectStatsByProvider(@Param("startTime") LocalDateTime startTime, 
                                                     @Param("endTime") LocalDateTime endTime);
}
