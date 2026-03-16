package com.litegateway.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.litegateway.admin.repository.entity.RewriteRule;

import java.util.List;

public interface RewriteRuleService extends IService<RewriteRule> {

    RewriteRule getByRuleId(String ruleId);

    List<RewriteRule> getAllEnabled();

    IPage<RewriteRule> queryPage(Page<RewriteRule> page, String ruleName,
                                 String matchType, String rewriteType, Integer enabled);

    List<RewriteRule> getByRouteId(String routeId);

    List<RewriteRule> getByServiceId(String serviceId);

    RewriteRule saveRule(RewriteRule rule);

    boolean updateRule(RewriteRule rule);

    boolean deleteRule(String ruleId);

    boolean updateStatus(String ruleId, Integer enabled);
}
