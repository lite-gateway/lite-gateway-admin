package com.litegateway.admin.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.litegateway.admin.repository.entity.CanaryRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 灰度规则 Mapper 接口
 */
@Mapper
public interface CanaryRuleMapper extends BaseMapper<CanaryRule> {

    /**
     * 根据规则ID查询
     */
    @Select("SELECT * FROM canary_rule WHERE rule_id = #{ruleId} AND deleted = 0")
    CanaryRule selectByRuleId(@Param("ruleId") String ruleId);

    /**
     * 查询所有启用的灰度规则
     */
    @Select("SELECT * FROM canary_rule WHERE enabled = 1 AND deleted = 0 ORDER BY create_time DESC")
    List<CanaryRule> selectAllEnabled();

    /**
     * 根据路由ID查询灰度规则
     */
    @Select("SELECT * FROM canary_rule WHERE route_id = #{routeId} AND enabled = 1 AND deleted = 0 ORDER BY canary_weight DESC")
    List<CanaryRule> selectByRouteId(@Param("routeId") String routeId);
}
