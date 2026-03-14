package com.litegateway.admin.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.litegateway.admin.repository.entity.CircuitBreakerRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 熔断规则 Mapper 接口
 */
@Mapper
public interface CircuitBreakerRuleMapper extends BaseMapper<CircuitBreakerRule> {

    /**
     * 根据规则ID查询
     */
    @Select("SELECT * FROM circuit_breaker_rule WHERE rule_id = #{ruleId} AND deleted = 0")
    CircuitBreakerRule selectByRuleId(@Param("ruleId") String ruleId);

    /**
     * 查询所有启用的熔断规则
     */
    @Select("SELECT * FROM circuit_breaker_rule WHERE enabled = 1 AND deleted = 0 ORDER BY create_time DESC")
    List<CircuitBreakerRule> selectAllEnabled();

    /**
     * 根据路由ID查询熔断规则
     */
    @Select("SELECT * FROM circuit_breaker_rule WHERE route_id = #{routeId} AND enabled = 1 AND deleted = 0")
    List<CircuitBreakerRule> selectByRouteId(@Param("routeId") String routeId);

    /**
     * 查询全局熔断规则
     */
    @Select("SELECT * FROM circuit_breaker_rule WHERE (route_id IS NULL OR route_id = '') AND enabled = 1 AND deleted = 0 LIMIT 1")
    CircuitBreakerRule selectGlobalRule();
}
