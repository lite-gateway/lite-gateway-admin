package com.litegateway.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.litegateway.admin.repository.entity.ApiInfo;
import com.litegateway.admin.repository.entity.ServiceInfo;
import com.litegateway.admin.repository.mapper.ApiInfoMapper;
import com.litegateway.admin.repository.mapper.ServiceInfoMapper;
import com.litegateway.admin.service.ApiInfoService;
import com.litegateway.admin.util.SwaggerParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * API信息服务实现类
 */
@Slf4j
@Service
public class ApiInfoServiceImpl extends ServiceImpl<ApiInfoMapper, ApiInfo>
        implements ApiInfoService {

    @Autowired
    private ApiInfoMapper apiInfoMapper;

    @Autowired
    private ServiceInfoMapper serviceInfoMapper;

    @Autowired
    private SwaggerParser swaggerParser;

    @Override
    public Page<ApiInfo> queryPage(String path, String method, Long serviceId, Integer status, int pageNum, int pageSize) {
        LambdaQueryWrapper<ApiInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ApiInfo::getDeleted, 0);

        if (StringUtils.isNotBlank(path)) {
            wrapper.like(ApiInfo::getPath, path);
        }
        if (StringUtils.isNotBlank(method)) {
            wrapper.eq(ApiInfo::getMethod, method);
        }
        if (serviceId != null) {
            wrapper.eq(ApiInfo::getServiceId, serviceId);
        }
        if (status != null) {
            wrapper.eq(ApiInfo::getStatus, status);
        }

        wrapper.orderByDesc(ApiInfo::getCreateTime);

        Page<ApiInfo> page = new Page<>(pageNum, pageSize);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public List<ApiInfo> listByServiceId(Long serviceId) {
        return apiInfoMapper.selectByServiceId(serviceId);
    }

    @Override
    public List<ApiInfo> listByServiceName(String serviceName) {
        return apiInfoMapper.selectByServiceName(serviceName);
    }

    @Override
    public List<ApiInfo> listPublished() {
        return apiInfoMapper.selectPublished();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publish(Long id) {
        ApiInfo api = baseMapper.selectById(id);
        if (api == null) {
            throw new RuntimeException("API不存在");
        }
        api.setStatus(1);
        api.setUpdateTime(LocalDateTime.now());
        baseMapper.updateById(api);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void offline(Long id) {
        ApiInfo api = baseMapper.selectById(id);
        if (api == null) {
            throw new RuntimeException("API不存在");
        }
        api.setStatus(2);
        api.setUpdateTime(LocalDateTime.now());
        baseMapper.updateById(api);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<ApiInfo> importFromSwagger(String swaggerUrl, Long serviceId) {
        log.info("开始从Swagger导入API: {}, serviceId: {}", swaggerUrl, serviceId);

        // 解析Swagger文档
        List<SwaggerParser.SwaggerApiInfo> swaggerApis = swaggerParser.parse(swaggerUrl);

        // 获取服务信息
        ServiceInfo service = serviceInfoMapper.selectById(serviceId);
        if (service == null) {
            throw new RuntimeException("服务不存在");
        }

        // 转换为API实体并保存
        List<ApiInfo> apis = swaggerApis.stream()
                .map(swaggerApi -> {
                    ApiInfo api = new ApiInfo();
                    api.setPath(swaggerApi.getPath());
                    api.setMethod(swaggerApi.getMethod());
                    api.setTitle(swaggerApi.getTitle());
                    api.setDescription(swaggerApi.getDescription());
                    api.setTags(swaggerApi.getTags());
                    api.setServiceId(serviceId);
                    api.setServiceName(service.getServiceName());
                    api.setVersion("v1");
                    api.setStatus(0); // 草稿状态
                    api.setRequireAuth(true);
                    api.setSwaggerSource(swaggerUrl);
                    api.setCreateTime(LocalDateTime.now());
                    api.setUpdateTime(LocalDateTime.now());
                    return api;
                })
                .collect(Collectors.toList());

        // 批量保存
        saveBatch(apis);

        log.info("成功导入{}个API", apis.size());
        return apis;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSave(List<ApiInfo> apis) {
        for (ApiInfo api : apis) {
            // 检查是否已存在
            ApiInfo existing = apiInfoMapper.selectByPathAndMethod(api.getPath(), api.getMethod());
            if (existing == null) {
                baseMapper.insert(api);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindRoute(Long apiId, Long routeId) {
        ApiInfo api = baseMapper.selectById(apiId);
        if (api == null) {
            throw new RuntimeException("API不存在");
        }
        api.setRouteId(routeId);
        api.setUpdateTime(LocalDateTime.now());
        baseMapper.updateById(api);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(ApiInfo entity) {
        if (entity.getStatus() == null) {
            entity.setStatus(0); // 默认草稿
        }
        if (entity.getRequireAuth() == null) {
            entity.setRequireAuth(true);
        }
        if (StringUtils.isBlank(entity.getVersion())) {
            entity.setVersion("v1");
        }
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        return super.save(entity);
    }
}
