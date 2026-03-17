package com.litegateway.admin.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.litegateway.admin.repository.entity.PolicyTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 策略模板Mapper
 */
@Mapper
public interface PolicyTemplateMapper extends BaseMapper<PolicyTemplate> {

    /**
     * 增加使用次数
     */
    @Update("UPDATE policy_template SET usage_count = usage_count + 1 WHERE id = #{templateId}")
    int incrementUsageCount(@Param("templateId") Long templateId);
}
