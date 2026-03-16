package com.litegateway.admin.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.litegateway.admin.repository.entity.DesensitizationRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DesensitizationRuleMapper extends BaseMapper<DesensitizationRule> {

    @Select("SELECT * FROM desensitization_rule WHERE rule_id = #{ruleId}")
    DesensitizationRule selectByRuleId(@Param("ruleId") String ruleId);

    @Select("SELECT * FROM desensitization_rule WHERE enabled = 1 ORDER BY priority DESC, create_time DESC")
    List<DesensitizationRule> selectAllEnabled();

    @Select("<script>" +
            "SELECT * FROM desensitization_rule " +
            "<where>" +
            "<if test='ruleName != null'> AND rule_name LIKE CONCAT('%', #{ruleName}, '%') </if>" +
            "<if test='dataType != null'> AND data_type = #{dataType} </if>" +
            "<if test='desensitizationType != null'> AND desensitization_type = #{desensitizationType} </if>" +
            "<if test='enabled != null'> AND enabled = #{enabled} </if>" +
            "</where>" +
            "ORDER BY priority DESC, create_time DESC" +
            "</script>")
    IPage<DesensitizationRule> selectByPage(Page<DesensitizationRule> page,
                                            @Param("ruleName") String ruleName,
                                            @Param("dataType") String dataType,
                                            @Param("desensitizationType") String desensitizationType,
                                            @Param("enabled") Integer enabled);

    @Select("SELECT * FROM desensitization_rule WHERE JSON_CONTAINS(route_ids, JSON_ARRAY(#{routeId})) AND enabled = 1 ORDER BY priority DESC")
    List<DesensitizationRule> selectByRouteId(@Param("routeId") String routeId);

    @Select("SELECT * FROM desensitization_rule WHERE JSON_CONTAINS(service_ids, JSON_ARRAY(#{serviceId})) AND enabled = 1 ORDER BY priority DESC")
    List<DesensitizationRule> selectByServiceId(@Param("serviceId") String serviceId);
}
