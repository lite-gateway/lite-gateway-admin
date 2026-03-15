package com.litegateway.admin.config;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingMaintainFactory;
import com.alibaba.nacos.api.naming.NamingMaintainService;
import com.alibaba.nacos.api.naming.NamingService;
import com.litegateway.admin.nacos.ServiceSubscriptionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * Nacos 命名服务配置
 * 配置 NamingService 和 NamingMaintainService 单例
 * 仅在 Nacos 启用时加载
 */
@Configuration
@Slf4j
@ConditionalOnProperty(name = "spring.cloud.nacos.discovery.enabled", havingValue = "true", matchIfMissing = false)
public class NacosNamingConfig {

    @Value("${spring.cloud.nacos.discovery.server-addr:localhost:8848}")
    private String serverAddr;

    @Value("${spring.cloud.nacos.discovery.namespace:}")
    private String namespace;

    @Value("${nacos.client.worker-threads:8}")
    private Integer workerThreads;

    /**
     * 主 NamingService - 用于查询和一般操作
     * 配置连接池参数（管理台需要大连接池）
     */
    @Bean(destroyMethod = "shutDown")
    public NamingService namingService() throws NacosException {
        Properties properties = new Properties();
        properties.put("serverAddr", serverAddr);
        properties.put("namespace", namespace);
        // 管理台需要更大的线程池处理并发查询
        properties.put("namingClientBeatThreadCount", String.valueOf(workerThreads));
        properties.put("namingPollingThreadCount", String.valueOf(workerThreads));
        // 设置超时时间
        properties.put("namingRequestDomainMaxRetryCount", "3");

        NamingService service = NamingFactory.createNamingService(properties);
        log.info("NamingService initialized for governance platform, server: {}", serverAddr);
        return service;
    }

    /**
     * 运维操作专用 MaintainService
     * 使用手动构建的 Properties，不依赖 NacosDiscoveryProperties bean
     */
    @Bean(destroyMethod = "shutDown")
    public NamingMaintainService namingMaintainService() throws NacosException {
        Properties properties = new Properties();
        properties.put("serverAddr", serverAddr);
        properties.put("namespace", namespace);
        return NamingMaintainFactory.createMaintainService(properties);
    }

    /**
     * Nacos 事件监听器
     * 已被 ServiceSubscriptionManager 取代，保留此 Bean 以保持兼容性
     */
    @Bean
    public NacosEventListener nacosEventListener(ServiceSubscriptionManager subscriptionManager) {
        log.info("NacosEventListener initialized (delegated to ServiceSubscriptionManager)");
        return new NacosEventListener();
    }
}
