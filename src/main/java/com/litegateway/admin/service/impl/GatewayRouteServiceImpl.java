package com.litegateway.admin.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.litegateway.admin.common.web.PageBody;
import com.litegateway.admin.constants.RedisTypeConstants;
import com.litegateway.admin.dto.InterfaceDTO;
import com.litegateway.admin.dto.RouteDTO;
import com.litegateway.admin.query.RouteQuery;
import com.litegateway.admin.repository.entity.GatewayRoute;
import com.litegateway.admin.repository.entity.ServiceInfo;
import com.litegateway.admin.repository.mapper.GatewayRouteMapper;
import com.litegateway.admin.service.ConfigService;
import com.litegateway.admin.service.GatewayRouteService;
import com.litegateway.admin.service.ServiceInfoService;
import com.litegateway.admin.vo.RouteVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 网关路由服务实现类
 * 负责路由的CRUD和管理
 * 注意：实例管理已移至 ServiceInstanceService
 */
@Slf4j
@Service
public class GatewayRouteServiceImpl extends ServiceImpl<GatewayRouteMapper, GatewayRoute> implements GatewayRouteService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ConfigService configService;

    @Autowired
    private ServiceInfoService serviceInfoService;

    @Override
    public void addRoute(RouteDTO routeDTO) {
        GatewayRoute route = new GatewayRoute();
        BeanUtils.copyProperties(routeDTO, route);

        // 生成唯一路由ID
        if (!StringUtils.hasText(route.getRouteId())) {
            route.setRouteId("route_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        }

        route.setCreateTime(LocalDateTime.now());
        route.setUpdateTime(LocalDateTime.now());
        route.setDeleted(0);

        this.save(route);
        log.info("Added route: {}", route.getName());

        // 增加配置版本号，通知 Core 模块更新
        configService.incrementVersion();
    }

    @Override
    public List<RouteDTO> routeList() {
        LambdaQueryWrapper<GatewayRoute> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GatewayRoute::getDeleted, 0);
        wrapper.orderByDesc(GatewayRoute::getCreateTime);

        List<GatewayRoute> routes = this.list(wrapper);
        return routes.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public void deleteRoute(Long id) {
        this.removeById(id);
        log.info("Deleted route: {}", id);

        // 增加配置版本号，通知 Core 模块更新
        configService.incrementVersion();
    }

    @Override
    public void updateRoute(RouteDTO routeDTO) {
        GatewayRoute route = new GatewayRoute();
        BeanUtils.copyProperties(routeDTO, route);
        route.setUpdateTime(LocalDateTime.now());

        this.updateById(route);
        log.info("Updated route: {}", route.getName());

        // 增加配置版本号，通知 Core 模块更新
        configService.incrementVersion();
    }

    @Override
    public RouteDTO getById(String id) {
        GatewayRoute route = this.getById(Long.valueOf(id));
        if (route == null) {
            return null;
        }
        return convertToDTO(route);
    }

    @Override
    public List<RouteVO> selectRouteList(RouteQuery query) {
        LambdaQueryWrapper<GatewayRoute> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GatewayRoute::getDeleted, 0);

        // 状态过滤
        if (StrUtil.isNotBlank(query.getStatus()) && !"2".equals(query.getStatus())) {
            wrapper.eq(GatewayRoute::getStatus, Integer.valueOf(query.getStatus()));
        }

        // 名称模糊查询
        if (StrUtil.isNotBlank(query.getName())) {
            wrapper.like(GatewayRoute::getName, query.getName());
        }

        // URI 模糊查询
        if (StrUtil.isNotBlank(query.getUri())) {
            wrapper.like(GatewayRoute::getUri, query.getUri());
        }

        // Path 模糊查询
        if (StrUtil.isNotBlank(query.getPath())) {
            wrapper.like(GatewayRoute::getPath, query.getPath());
        }

        wrapper.orderByDesc(GatewayRoute::getCreateTime);

        List<GatewayRoute> list = this.list(wrapper);

        return list.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public void reloadConfig() {
        // 发布 Redis 消息通知网关刷新路由
        redisTemplate.convertAndSend(RedisTypeConstants.CHANNEL, RedisTypeConstants.ROUTE_UPDATE);
        log.info("Published route update message to Redis channel");
    }

    @Override
    public PageBody<RouteVO> selectRoutePageVo(RouteQuery query) {
        LambdaQueryWrapper<GatewayRoute> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GatewayRoute::getDeleted, 0);

        // 状态过滤
        if (StrUtil.isNotBlank(query.getStatus()) && !"2".equals(query.getStatus())) {
            wrapper.eq(GatewayRoute::getStatus, Integer.valueOf(query.getStatus()));
        }

        // 名称模糊查询
        if (StrUtil.isNotBlank(query.getName())) {
            wrapper.like(GatewayRoute::getName, query.getName());
        }

        // URI 模糊查询
        if (StrUtil.isNotBlank(query.getUri())) {
            wrapper.like(GatewayRoute::getUri, query.getUri());
        }

        // Path 模糊查询
        if (StrUtil.isNotBlank(query.getPath())) {
            wrapper.like(GatewayRoute::getPath, query.getPath());
        }

        wrapper.orderByDesc(GatewayRoute::getCreateTime);

        Page<GatewayRoute> page = new Page<>(query.getPageNum(), query.getPageSize());
        Page<GatewayRoute> resultPage = this.page(page, wrapper);

        List<RouteVO> voList = resultPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return new PageBody<>(
                resultPage.getTotal(),
                (int) resultPage.getPages(),
                query.getPageSize(),
                query.getPageNum(),
                voList
        );
    }

    @Override
    public List<InterfaceDTO> mapToInterfaceDTO(String result, String path, String summary, String prePath) {
        String newPrePath = prePath.replace("/**", "");
        List<InterfaceDTO> faces = new ArrayList<>();

        try {
            JsonNode root = objectMapper.readTree(result);
            JsonNode pathsNode = root.get("paths");

            if (pathsNode == null || !pathsNode.isObject()) {
                return faces;
            }

            Iterator<String> fieldNames = pathsNode.fieldNames();
            while (fieldNames.hasNext()) {
                String p = fieldNames.next();

                // 路径过滤
                if (path != null && !path.isEmpty() && !p.contains(path)) {
                    continue;
                }

                JsonNode pathNode = pathsNode.get(p);
                if (!pathNode.isObject()) {
                    continue;
                }

                // 获取第一个 HTTP 方法
                Iterator<String> methods = pathNode.fieldNames();
                if (!methods.hasNext()) {
                    continue;
                }

                String type = methods.next();
                JsonNode methodNode = pathNode.get(type);

                InterfaceDTO dto = new InterfaceDTO();
                dto.setPath(p);
                dto.setType(type.toUpperCase());

                if (methodNode.has("tags") && methodNode.get("tags").isArray() && methodNode.get("tags").size() > 0) {
                    dto.setTag(methodNode.get("tags").get(0).asText());
                }

                String summaryResult = methodNode.has("summary") ? methodNode.get("summary").asText() : "";

                // 摘要过滤
                if (summary != null && !summary.isEmpty()) {
                    if (summaryResult.contains(summary)) {
                        dto.setSummary(summaryResult);
                        faces.add(dto);
                    }
                } else {
                    dto.setSummary(summaryResult);
                    faces.add(dto);
                }
            }

            // 添加前缀路径并标记是否已添加
            faces.forEach(x -> x.setPath(newPrePath.concat(x.getPath())));

        } catch (Exception e) {
            log.error("Failed to parse interface info: {}", e.getMessage());
        }

        return faces;
    }

    // ==================== 私有方法 ====================

    private RouteDTO convertToDTO(GatewayRoute route) {
        RouteDTO dto = new RouteDTO();
        BeanUtils.copyProperties(route, dto);
        dto.setId(route.getId());
        dto.setStatus(String.valueOf(route.getStatus()));
        return dto;
    }

    private RouteVO convertToVO(GatewayRoute route) {
        RouteVO vo = new RouteVO();
        BeanUtils.copyProperties(route, vo);
        vo.setId(route.getId());
        vo.setStatus(String.valueOf(route.getStatus()));

        // 填充服务名称
        if (route.getServiceId() != null) {
            vo.setServiceId(route.getServiceId());
            ServiceInfo service = serviceInfoService.getById(route.getServiceId());
            Optional.ofNullable(service).ifPresent(serviceInfo -> {
                vo.setServiceName(serviceInfo.getServiceName());
            });
        }

        return vo;
    }
}
