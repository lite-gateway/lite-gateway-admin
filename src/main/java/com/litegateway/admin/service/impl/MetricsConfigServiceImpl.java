package com.litegateway.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.litegateway.admin.repository.entity.AlertRule;
import com.litegateway.admin.repository.entity.MetricsConfig;
import com.litegateway.admin.repository.mapper.AlertRuleMapper;
import com.litegateway.admin.repository.mapper.MetricsConfigMapper;
import com.litegateway.admin.service.MetricsConfigService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class MetricsConfigServiceImpl extends ServiceImpl<MetricsConfigMapper, MetricsConfig>
        implements MetricsConfigService {

    @Autowired
    private AlertRuleMapper alertRuleMapper;

    @Override
    public MetricsConfig getActiveConfig() {
        return baseMapper.selectActiveConfig();
    }

    @Override
    @Transactional
    public MetricsConfig saveOrUpdateConfig(MetricsConfig config) {
        if (StringUtils.isBlank(config.getConfigId())) {
            config.setConfigId("metrics_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        }
        if (config.getStatus() == null) {
            config.setStatus(1);
        }

        MetricsConfig existing = getActiveConfig();
        if (existing != null) {
            config.setId(existing.getId());
            updateById(config);
        } else {
            save(config);
        }
        return config;
    }

    @Override
    public Page<AlertRule> queryAlertRulePage(String ruleName, Integer status, int pageNum, int pageSize) {
        LambdaQueryWrapper<AlertRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlertRule::getDeleted, 0);

        if (StringUtils.isNotBlank(ruleName)) {
            wrapper.like(AlertRule::getRuleName, ruleName);
        }
        if (status != null) {
            wrapper.eq(AlertRule::getStatus, status);
        }

        wrapper.orderByDesc(AlertRule::getCreateTime);

        Page<AlertRule> page = new Page<>(pageNum, pageSize);
        return alertRuleMapper.selectPage(page, wrapper);
    }

    @Override
    public AlertRule getAlertRuleById(Long id) {
        return alertRuleMapper.selectById(id);
    }

    @Override
    public List<AlertRule> listAllEnabledAlertRules() {
        return alertRuleMapper.selectAllEnabled();
    }

    @Override
    @Transactional
    public void saveAlertRule(AlertRule rule) {
        if (StringUtils.isBlank(rule.getRuleId())) {
            rule.setRuleId("alert_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        }
        if (rule.getStatus() == null) {
            rule.setStatus(1);
        }
        alertRuleMapper.insert(rule);
    }

    @Override
    @Transactional
    public void updateAlertRule(AlertRule rule) {
        alertRuleMapper.updateById(rule);
    }

    @Override
    @Transactional
    public void deleteAlertRule(Long id) {
        alertRuleMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void updateAlertRuleStatus(Long id, Integer status) {
        AlertRule rule = new AlertRule();
        rule.setId(id);
        rule.setStatus(status);
        alertRuleMapper.updateById(rule);
    }
}
