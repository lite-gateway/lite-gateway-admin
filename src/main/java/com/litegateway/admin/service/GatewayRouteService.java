package com.litegateway.admin.service;

import com.litegateway.admin.common.web.PageBody;
import com.litegateway.admin.dto.InterfaceDTO;
import com.litegateway.admin.dto.RouteDTO;
import com.litegateway.admin.query.RouteQuery;
import com.litegateway.admin.vo.RouteVO;

import java.util.List;

/**
 * 网关路由服务接口
 * 负责路由的CRUD和管理
 * 注意：实例管理已移至 ServiceInstanceService
 */
public interface GatewayRouteService {

    /**
     * 添加路由规则
     * @param routeDTO 路由DTO
     */
    void addRoute(RouteDTO routeDTO);

    /**
     * 获取路由列表
     * @return 路由列表
     */
    List<RouteDTO> routeList();

    /**
     * 删除路由
     * @param id 路由ID
     */
    void deleteRoute(Long id);

    /**
     * 重新加载配置
     */
    void reloadConfig();

    /**
     * 分页查询路由
     * @param query 查询参数
     * @return 分页结果
     */
    PageBody<RouteVO> selectRoutePageVo(RouteQuery query);

    /**
     * 解析接口信息
     * @param result Swagger API Docs JSON
     * @param path 路径过滤
     * @param summary 摘要过滤
     * @param prePath 前缀路径
     * @return 接口列表
     */
    List<InterfaceDTO> mapToInterfaceDTO(String result, String path, String summary, String prePath);

    /**
     * 更新路由
     * @param routeDTO 路由DTO
     */
    void updateRoute(RouteDTO routeDTO);

    /**
     * 根据ID获取路由
     * @param id 路由ID
     * @return 路由DTO
     */
    RouteDTO getById(String id);

    /**
     * 查询路由列表
     * @param query 查询参数
     * @return 路由列表
     */
    List<RouteVO> selectRouteList(RouteQuery query);
}
