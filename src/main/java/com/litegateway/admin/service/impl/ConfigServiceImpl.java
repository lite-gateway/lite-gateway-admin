package com.litegateway.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.litegateway.admin.dto.*;
import com.litegateway.admin.repository.entity.*;
import com.litegateway.admin.repository.mapper.*;
import com.litegateway.admin.service.ServiceInfoService;
import com.litegateway.admin.service.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 配置服务实现类
 */
@Slf4j
@Service
public class ConfigServiceImpl implements ConfigService {

    @Autowired
    private GatewayRouteMapper routeMapper;

    @Autowired
    private IpBlacklistMapper ipBlacklistMapper;

    @Autowired
    private WhiteListMapper whiteListMapper;

    @Autowired
    private GatewayFeatureConfigMapper featureConfigMapper;

    @Autowired
    private CircuitBreakerRuleMapper circuitBreakerRuleMapper;

    @Autowired
    private CanaryRuleMapper canaryRuleMapper;

    @Autowired
    private ServiceInfoService serviceInfoService;

    // 配置版本号，每次配置变更时递增
    private final AtomicLong configVersion = new AtomicLong(System.currentTimeMillis());

    @Override
    public GatewayConfigDTO getGatewayConfig() {
        GatewayConfigDTO config = new GatewayConfigDTO();
        config.setVersion(configVersion.get());

        // 获取启用的路由列表
        List<GatewayRoute> routes = routeMapper.selectEnabledRoutes();
        List<RouteDTO> routeDTOs = routes.stream().map(this::convertToRouteDTO).collect(Collectors.toList());
        config.setRoutes(routeDTOs);

        // 获取IP黑名单
        LambdaQueryWrapper<IpBlacklist> ipWrapper = new LambdaQueryWrapper<>();
        ipWrapper.eq(IpBlacklist::getDeleted, 0);
        List<IpBlacklist> ipList = ipBlacklistMapper.selectList(ipWrapper);
        List<IpBlackDTO> ipBlackDTOs = ipList.stream().map(this::convertToIpBlackDTO).collect(Collectors.toList());
        config.setIpBlacklist(ipBlackDTOs);

        // 获取白名单
        LambdaQueryWrapper<WhiteList> whiteWrapper = new LambdaQueryWrapper<>();
        whiteWrapper.eq(WhiteList::getDeleted, 0);
        List<WhiteList> whiteList = whiteListMapper.selectList(whiteWrapper);
        List<WhiteListDTO> whiteListDTOs = whiteList.stream().map(this::convertToWhiteListDTO).collect(Collectors.toList());
        config.setWhiteList(whiteListDTOs);

        // 获取功能配置
        LambdaQueryWrapper<GatewayFeatureConfig> featureWrapper = new LambdaQueryWrapper<>();
        featureWrapper.eq(GatewayFeatureConfig::getDeleted, 0)
                .eq(GatewayFeatureConfig::getEnabled, true);
        List<GatewayFeatureConfig> featureConfigs = featureConfigMapper.selectList(featureWrapper);
        List<FeatureConfigDTO> featureConfigDTOs = featureConfigs.stream()
                .map(this::convertToFeatureConfigDTO)
                .collect(Collectors.toList());
        config.setFeatureConfigs(featureConfigDTOs);

        // 获取熔断规则
        LambdaQueryWrapper<CircuitBreakerRule> cbWrapper = new LambdaQueryWrapper<>();
        cbWrapper.eq(CircuitBreakerRule::getDeleted, 0)
                .eq(CircuitBreakerRule::getEnabled, true);
        List<CircuitBreakerRule> circuitBreakerRules = circuitBreakerRuleMapper.selectList(cbWrapper);
        List<CircuitBreakerRuleDTO> circuitBreakerRuleDTOs = circuitBreakerRules.stream()
                .map(this::convertToCircuitBreakerRuleDTO)
                .collect(Collectors.toList());
        config.setCircuitBreakerRules(circuitBreakerRuleDTOs);

        // 获取灰度规则
        LambdaQueryWrapper<CanaryRule> canaryWrapper = new LambdaQueryWrapper<>();
        canaryWrapper.eq(CanaryRule::getDeleted, 0)
                .eq(CanaryRule::getEnabled, true);
        List<CanaryRule> canaryRules = canaryRuleMapper.selectList(canaryWrapper);
        List<CanaryRuleDTO> canaryRuleDTOs = canaryRules.stream()
                .map(this::convertToCanaryRuleDTO)
                .collect(Collectors.toList());
        config.setCanaryRules(canaryRuleDTOs);

        // 获取服务列表（只包含在线服务）
        List<ServiceInfo> services = serviceInfoService.listAllOnline();
        List<ServiceInfoDTO> serviceDTOs = services.stream()
                .map(this::convertToServiceInfoDTO)
                .collect(Collectors.toList());
        config.setServices(serviceDTOs);

        log.info("Returning gateway config: version={}, routes={}, ipBlacklist={}, whiteList={}, features={}, circuitBreakers={}, canaries={}, services={}",
                config.getVersion(), routeDTOs.size(), ipBlackDTOs.size(), whiteListDTOs.size(),
                featureConfigDTOs.size(), circuitBreakerRuleDTOs.size(), canaryRuleDTOs.size(), serviceDTOs.size());

        return config;
    }

    @Override
    public Long getConfigVersion() {
        return configVersion.get();
    }

    @Override
    public void incrementVersion() {
        long newVersion = configVersion.incrementAndGet();
        log.info("Config version incremented to: {}", newVersion);
    }

    private RouteDTO convertToRouteDTO(GatewayRoute route) {
        RouteDTO dto = new RouteDTO();
        BeanUtils.copyProperties(route, dto);
        dto.setId(route.getId());
        dto.setStatus(String.valueOf(route.getStatus()));
        return dto;
    }

    private IpBlackDTO convertToIpBlackDTO(IpBlacklist ip) {
        IpBlackDTO dto = new IpBlackDTO();
        BeanUtils.copyProperties(ip, dto);
        return dto;
    }

    private WhiteListDTO convertToWhiteListDTO(WhiteList white) {
        WhiteListDTO dto = new WhiteListDTO();
        BeanUtils.copyProperties(white, dto);
        return dto;
    }

    private FeatureConfigDTO convertToFeatureConfigDTO(GatewayFeatureConfig config) {
        FeatureConfigDTO dto = new FeatureConfigDTO();
        BeanUtils.copyProperties(config, dto);
        return dto;
    }

    private CircuitBreakerRuleDTO convertToCircuitBreakerRuleDTO(CircuitBreakerRule rule) {
        CircuitBreakerRuleDTO dto = new CircuitBreakerRuleDTO();
        BeanUtils.copyProperties(rule, dto);
        return dto;
    }

    private CanaryRuleDTO convertToCanaryRuleDTO(CanaryRule rule) {
        CanaryRuleDTO dto = new CanaryRuleDTO();
        BeanUtils.copyProperties(rule, dto);
        return dto;
    }

    private ServiceInfoDTO convertToServiceInfoDTO(ServiceInfo service) {
        ServiceInfoDTO dto = new ServiceInfoDTO();
        BeanUtils.copyProperties(service, dto);
        if (service.getLastSyncTime() != null) {
            dto.setLastSyncTime(service.getLastSyncTime().toString());
        }
        return dto;
    }
}
