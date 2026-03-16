package com.litegateway.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.litegateway.admin.repository.entity.DesensitizationRule;

import java.util.List;

public interface DesensitizationRuleService extends IService<DesensitizationRule> {

    DesensitizationRule getByRuleId(String ruleId);

    List<DesensitizationRule> getAllEnabled();

    IPage<DesensitizationRule> queryPage(Page<DesensitizationRule> page, String ruleName,
                                         String dataType, String desensitizationType, Integer enabled);

    List<DesensitizationRule> getByRouteId(String routeId);

    List<DesensitizationRule> getByServiceId(String serviceId);

    DesensitizationRule saveRule(DesensitizationRule rule);

    boolean updateRule(DesensitizationRule rule);

    boolean deleteRule(String ruleId);

    boolean updateStatus(String ruleId, Integer enabled);
}
