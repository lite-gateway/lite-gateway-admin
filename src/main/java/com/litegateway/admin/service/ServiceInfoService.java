package com.litegateway.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.litegateway.admin.repository.entity.ServiceInfo;

import java.util.List;

/**
 * 服务信息服务接口
 */
public interface ServiceInfoService extends IService<ServiceInfo> {

    /**
     * 分页查询服务列表
     */
    Page<ServiceInfo> queryPage(String serviceName, String groupName, Integer status, int pageNum, int pageSize);

    /**
     * 根据服务名查询
     */
    ServiceInfo getByServiceName(String serviceName);

    /**
     * 查询所有在线服务
     */
    List<ServiceInfo> listAllOnline();

    /**
     * 从Nacos同步服务列表
     */
    void syncFromNacos();

    /**
     * 同步指定服务的实例信息
     */
    void syncServiceInstances(String serviceName);

    /**
     * 更新服务显示的Swagger URL
     */
    void updateSwaggerUrl(Long id, String swaggerUrl);

    /**
     * 更新服务元数据
     */
    void updateMetadata(Long id, String metadata);

    /**
     * 更新服务的API数量统计
     */
    void updateApiCount(Long serviceId);

    /**
     * 更新服务的路由数量统计
     */
    void updateRouteCount(Long serviceId);
}
