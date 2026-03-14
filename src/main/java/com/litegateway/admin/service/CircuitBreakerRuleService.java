package com.litegateway.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.litegateway.admin.repository.entity.CircuitBreakerRule;

import java.util.List;

/**
 * 熔断规则服务接口
 */
public interface CircuitBreakerRuleService extends IService<CircuitBreakerRule> {

    /**
     * 分页查询熔断规则
     */
    Page<CircuitBreakerRule> queryPage(String ruleName, String routeId, Boolean enabled, int pageNum, int pageSize);

    /**
     * 根据规则ID查询
     */
    CircuitBreakerRule getByRuleId(String ruleId);

    /**
     * 查询所有启用的熔断规则
     */
    List<CircuitBreakerRule> listAllEnabled();

    /**
     * 根据路由ID查询熔断规则
     */
    List<CircuitBreakerRule> listByRouteId(String routeId);

    /**
     * 获取全局熔断规则
     */
    CircuitBreakerRule getGlobalRule();

    /**
     * 更新熔断规则状态
     */
    void updateStatus(Long id, Boolean enabled);

    /**
     * 发布熔断规则变更事件
     */
    void publishRuleChangeEvent(String ruleId);
}
