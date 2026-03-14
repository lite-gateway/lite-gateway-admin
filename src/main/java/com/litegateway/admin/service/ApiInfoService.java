package com.litegateway.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.litegateway.admin.dto.ApiInfoDTO;
import com.litegateway.admin.repository.entity.ApiInfo;

import java.util.List;

/**
 * API信息服务接口
 */
public interface ApiInfoService extends IService<ApiInfo> {

    /**
     * 分页查询API列表
     */
    Page<ApiInfo> queryPage(String path, String method, Long serviceId, Integer status, int pageNum, int pageSize);

    /**
     * 根据服务ID查询API列表
     */
    List<ApiInfo> listByServiceId(Long serviceId);

    /**
     * 根据服务名查询API列表
     */
    List<ApiInfo> listByServiceName(String serviceName);

    /**
     * 查询已发布的API列表
     */
    List<ApiInfo> listPublished();

    /**
     * 发布API
     */
    void publish(Long id);

    /**
     * 下线API
     */
    void offline(Long id);

    /**
     * 从Swagger URL导入API
     */
    List<ApiInfo> importFromSwagger(String swaggerUrl, Long serviceId);

    /**
     * 批量保存API
     */
    void batchSave(List<ApiInfo> apis);

    /**
     * 关联API到路由
     */
    void bindRoute(Long apiId, Long routeId);
}
