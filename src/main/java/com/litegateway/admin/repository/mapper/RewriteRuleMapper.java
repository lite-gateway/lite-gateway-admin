package com.litegateway.admin.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.litegateway.admin.repository.entity.RewriteRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RewriteRuleMapper extends BaseMapper<RewriteRule> {

    @Select("SELECT * FROM rewrite_rule WHERE rule_id = #{ruleId}")
    RewriteRule selectByRuleId(@Param("ruleId") String ruleId);

    @Select("SELECT * FROM rewrite_rule WHERE enabled = 1 ORDER BY priority DESC, create_time DESC")
    List<RewriteRule> selectAllEnabled();

    @Select("<script>" +
            "SELECT * FROM rewrite_rule " +
            "<where>" +
            "<if test='ruleName != null'> AND rule_name LIKE CONCAT('%', #{ruleName}, '%') </if>" +
            "<if test='matchType != null'> AND match_type = #{matchType} </if>" +
            "<if test='rewriteType != null'> AND rewrite_type = #{rewriteType} </if>" +
            "<if test='enabled != null'> AND enabled = #{enabled} </if>" +
            "</where>" +
            "ORDER BY priority DESC, create_time DESC" +
            "</script>")
    IPage<RewriteRule> selectByPage(Page<RewriteRule> page,
                                    @Param("ruleName") String ruleName,
                                    @Param("matchType") String matchType,
                                    @Param("rewriteType") String rewriteType,
                                    @Param("enabled") Integer enabled);

    @Select("SELECT * FROM rewrite_rule WHERE JSON_CONTAINS(route_ids, JSON_ARRAY(#{routeId})) AND enabled = 1 ORDER BY priority DESC")
    List<RewriteRule> selectByRouteId(@Param("routeId") String routeId);

    @Select("SELECT * FROM rewrite_rule WHERE JSON_CONTAINS(service_ids, JSON_ARRAY(#{serviceId})) AND enabled = 1 ORDER BY priority DESC")
    List<RewriteRule> selectByServiceId(@Param("serviceId") String serviceId);
}
