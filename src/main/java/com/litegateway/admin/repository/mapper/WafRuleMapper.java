package com.litegateway.admin.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.litegateway.admin.repository.entity.WafRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * WAF规则Mapper
 */
@Mapper
public interface WafRuleMapper extends BaseMapper<WafRule> {

    /**
     * 增加命中次数
     */
    @Update("UPDATE waf_rule SET hit_count = hit_count + 1 WHERE id = #{ruleId}")
    int incrementHitCount(@Param("ruleId") Long ruleId);
}
