package com.litegateway.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.litegateway.admin.repository.entity.AlertRule;
import com.litegateway.admin.repository.entity.MetricsConfig;

import java.util.List;

public interface MetricsConfigService extends IService<MetricsConfig> {

    MetricsConfig getActiveConfig();

    MetricsConfig saveOrUpdateConfig(MetricsConfig config);

    Page<AlertRule> queryAlertRulePage(String ruleName, Integer status, int pageNum, int pageSize);

    AlertRule getAlertRuleById(Long id);

    List<AlertRule> listAllEnabledAlertRules();

    void saveAlertRule(AlertRule rule);

    void updateAlertRule(AlertRule rule);

    void deleteAlertRule(Long id);

    void updateAlertRuleStatus(Long id, Integer status);
}
