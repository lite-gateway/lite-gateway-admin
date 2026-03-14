package com.litegateway.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.litegateway.admin.repository.entity.CanaryRule;

import java.util.List;

/**
 * 灰度规则服务接口
 */
public interface CanaryRuleService extends IService<CanaryRule> {

    /**
     * 分页查询灰度规则
     */
    Page<CanaryRule> queryPage(String ruleName, String routeId, Boolean enabled, int pageNum, int pageSize);

    /**
     * 根据规则ID查询
     */
    CanaryRule getByRuleId(String ruleId);

    /**
     * 查询所有启用的灰度规则
     */
    List<CanaryRule> listAllEnabled();

    /**
     * 根据路由ID查询灰度规则
     */
    List<CanaryRule> listByRouteId(String routeId);

    /**
     * 更新灰度规则状态
     */
    void updateStatus(Long id, Boolean enabled);

    /**
     * 发布灰度规则变更事件
     */
    void publishRuleChangeEvent(String ruleId);
}
