package com.litegateway.admin.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.litegateway.admin.repository.entity.RateLimitRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 限流规则 Mapper 接口
 */
@Mapper
public interface RateLimitRuleMapper extends BaseMapper<RateLimitRule> {

    /**
     * 根据规则ID查询
     */
    @Select("SELECT * FROM rate_limit_rule WHERE rule_id = #{ruleId} AND deleted = 0")
    RateLimitRule selectByRuleId(@Param("ruleId") String ruleId);

    /**
     * 查询所有启用的限流规则
     */
    @Select("SELECT * FROM rate_limit_rule WHERE status = 1 AND deleted = 0 ORDER BY create_time DESC")
    List<RateLimitRule> selectAllEnabled();

    /**
     * 根据路由ID查询关联的限流规则
     */
    @Select("SELECT r.* FROM rate_limit_rule r " +
            "INNER JOIN rate_limit_route_relation rel ON r.rule_id = rel.rate_limit_rule_id " +
            "WHERE rel.route_id = #{routeId} AND r.status = 1 AND r.deleted = 0")
    List<RateLimitRule> selectByRouteId(@Param("routeId") String routeId);
}
