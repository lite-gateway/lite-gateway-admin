package com.litegateway.admin.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.litegateway.admin.repository.entity.AlertRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AlertRuleMapper extends BaseMapper<AlertRule> {

    @Select("SELECT * FROM alert_rule WHERE rule_id = #{ruleId} AND deleted = 0")
    AlertRule selectByRuleId(@Param("ruleId") String ruleId);

    @Select("SELECT * FROM alert_rule WHERE status = 1 AND deleted = 0 ORDER BY create_time DESC")
    List<AlertRule> selectAllEnabled();
}
