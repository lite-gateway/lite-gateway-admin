package com.litegateway.admin.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.litegateway.admin.repository.entity.RateLimitRouteRelation;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 限流规则与路由关联 Mapper 接口
 */
@Mapper
public interface RateLimitRouteRelationMapper extends BaseMapper<RateLimitRouteRelation> {

    /**
     * 根据限流规则ID查询关联的路由ID列表
     */
    @Select("SELECT route_id FROM rate_limit_route_relation WHERE rate_limit_rule_id = #{ruleId}")
    List<String> selectRouteIdsByRuleId(@Param("ruleId") String ruleId);

    /**
     * 根据路由ID查询关联的限流规则ID列表
     */
    @Select("SELECT rate_limit_rule_id FROM rate_limit_route_relation WHERE route_id = #{routeId}")
    List<String> selectRuleIdsByRouteId(@Param("routeId") String routeId);

    /**
     * 根据限流规则ID删除所有关联
     */
    @Delete("DELETE FROM rate_limit_route_relation WHERE rate_limit_rule_id = #{ruleId}")
    int deleteByRuleId(@Param("ruleId") String ruleId);

    /**
     * 根据路由ID删除所有关联
     */
    @Delete("DELETE FROM rate_limit_route_relation WHERE route_id = #{routeId}")
    int deleteByRouteId(@Param("routeId") String routeId);

    /**
     * 删除指定限流规则与路由的关联
     */
    @Delete("DELETE FROM rate_limit_route_relation WHERE rate_limit_rule_id = #{ruleId} AND route_id = #{routeId}")
    int deleteByRuleIdAndRouteId(@Param("ruleId") String ruleId, @Param("routeId") String routeId);
}
