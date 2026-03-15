package com.litegateway.admin.nacos;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.ListView;
import com.litegateway.admin.cache.InstanceCacheManager;
import com.litegateway.admin.websocket.ServiceChangeWebSocketHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Nacos 服务订阅管理器
 * 负责管理所有服务的订阅、事件监听和缓存更新
 * 仅在 Nacos 启用时加载
 */
@Slf4j
@Component
@ConditionalOnBean(NamingService.class)
public class ServiceSubscriptionManager {

    @Autowired
    private NamingService namingService;

    @Autowired
    private InstanceCacheManager cacheManager;

    @Autowired(required = false)
    private ServiceChangeWebSocketHandler webSocketHandler;

    /**
     * 已订阅的服务集合
     */
    private final Set<String> subscribedServices = ConcurrentHashMap.newKeySet();

    /**
     * 服务监听器映射
     */
    private final Map<String, EventListener> serviceListeners = new ConcurrentHashMap<>();

    /**
     * 定时刷新线程池
     */
    private ScheduledExecutorService refreshExecutor;

    /**
     * 是否运行中
     */
    private volatile boolean running = false;

    @PostConstruct
    public void init() {
        log.info("Initializing ServiceSubscriptionManager...");
        this.refreshExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "nacos-service-refresh");
            t.setDaemon(true);
            return t;
        });
        this.running = true;

        // 延迟5秒后启动，等待应用完全初始化
        refreshExecutor.schedule(this::discoverAndSubscribeServices, 5, TimeUnit.SECONDS);

        // 每30秒检查一次新服务
        refreshExecutor.scheduleWithFixedDelay(
                this::discoverAndSubscribeServices, 30, 30, TimeUnit.SECONDS);

        log.info("ServiceSubscriptionManager initialized");
    }

    @PreDestroy
    public void destroy() {
        log.info("Destroying ServiceSubscriptionManager...");
        this.running = false;

        if (refreshExecutor != null) {
            refreshExecutor.shutdown();
            try {
                if (!refreshExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    refreshExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                refreshExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        // 取消所有订阅
        for (String serviceName : subscribedServices) {
            try {
                namingService.unsubscribe(serviceName, serviceListeners.get(serviceName));
                log.debug("Unsubscribed from service: {}", serviceName);
            } catch (NacosException e) {
                log.warn("Failed to unsubscribe from service: {}", serviceName, e);
            }
        }

        subscribedServices.clear();
        serviceListeners.clear();
        log.info("ServiceSubscriptionManager destroyed");
    }

    /**
     * 发现并订阅所有服务
     */
    public void discoverAndSubscribeServices() {
        if (!running) {
            return;
        }

        try {
            // 获取所有服务名
            ListView<String> servicesView = namingService.getServicesOfServer(1, Integer.MAX_VALUE);
            List<String> allServices = servicesView.getData();

            log.debug("Discovered {} services from Nacos", allServices.size());

            // 订阅新服务
            for (String serviceName : allServices) {
                if (!subscribedServices.contains(serviceName)) {
                    subscribeService(serviceName);
                }
            }

            // 更新服务列表缓存
            cacheManager.putServices("all_services", allServices);

        } catch (NacosException e) {
            log.error("Failed to discover services from Nacos", e);
        }
    }

    /**
     * 订阅指定服务
     */
    public void subscribeService(String serviceName) {
        if (subscribedServices.contains(serviceName)) {
            return;
        }

        try {
            // 创建事件监听器
            EventListener listener = event -> {
                if (event instanceof NamingEvent) {
                    NamingEvent namingEvent = (NamingEvent) event;
                    handleServiceChange(namingEvent.getServiceName(), namingEvent.getInstances());
                }
            };

            // 订阅服务
            namingService.subscribe(serviceName, listener);

            // 保存订阅信息
            subscribedServices.add(serviceName);
            serviceListeners.put(serviceName, listener);

            // 立即获取一次实例列表并缓存
            List<Instance> instances = namingService.getAllInstances(serviceName);
            cacheManager.putInstances(serviceName, instances);

            log.info("Subscribed to service: {} ({} instances)", serviceName, instances.size());

        } catch (NacosException e) {
            log.error("Failed to subscribe to service: {}", serviceName, e);
        }
    }

    /**
     * 取消订阅服务
     */
    public void unsubscribeService(String serviceName) {
        if (!subscribedServices.contains(serviceName)) {
            return;
        }

        try {
            EventListener listener = serviceListeners.remove(serviceName);
            if (listener != null) {
                namingService.unsubscribe(serviceName, listener);
            }
            subscribedServices.remove(serviceName);
            cacheManager.invalidate(serviceName);

            log.info("Unsubscribed from service: {}", serviceName);
        } catch (NacosException e) {
            log.error("Failed to unsubscribe from service: {}", serviceName, e);
        }
    }

    /**
     * 处理服务变更事件
     */
    private void handleServiceChange(String serviceName, List<Instance> instances) {
        log.info("Service changed: {} ({} instances)", serviceName, instances.size());

        // 1. 更新本地缓存
        cacheManager.putInstances(serviceName, instances);

        // 2. 通过 WebSocket 推送给前端
        if (webSocketHandler != null) {
            webSocketHandler.broadcastServiceChange(serviceName, instances);
        }
    }

    /**
     * 手动刷新指定服务的实例列表
     */
    public void refreshService(String serviceName) {
        try {
            List<Instance> instances = namingService.getAllInstances(serviceName);
            cacheManager.putInstances(serviceName, instances);
            log.info("Manually refreshed service: {} ({} instances)", serviceName, instances.size());
        } catch (NacosException e) {
            log.error("Failed to refresh service: {}", serviceName, e);
        }
    }

    /**
     * 获取已订阅的服务列表
     */
    public Set<String> getSubscribedServices() {
        return Set.copyOf(subscribedServices);
    }

    /**
     * 检查是否已订阅指定服务
     */
    public boolean isSubscribed(String serviceName) {
        return subscribedServices.contains(serviceName);
    }
}
