package com.litegateway.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.litegateway.admin.repository.entity.RateLimitRule;

import java.util.List;

/**
 * 限流规则服务接口
 */
public interface RateLimitRuleService extends IService<RateLimitRule> {

    /**
     * 分页查询限流规则
     */
    Page<RateLimitRule> queryPage(String ruleName, Integer limitType, Integer status, int pageNum, int pageSize);

    /**
     * 根据规则ID查询
     */
    RateLimitRule getByRuleId(String ruleId);

    /**
     * 查询所有启用的限流规则
     */
    List<RateLimitRule> listAllEnabled();

    /**
     * 根据路由ID查询关联的限流规则
     */
    List<RateLimitRule> listByRouteId(String routeId);

    /**
     * 获取限流规则已绑定的路由ID列表
     */
    List<String> getBoundRouteIds(String ruleId);

    /**
     * 绑定路由到限流规则
     */
    void bindRoutes(String ruleId, List<String> routeIds);

    /**
     * 解绑路由从限流规则
     */
    void unbindRoute(String ruleId, String routeId);

    /**
     * 更新限流规则状态
     */
    void updateStatus(Long id, Integer status);
}
