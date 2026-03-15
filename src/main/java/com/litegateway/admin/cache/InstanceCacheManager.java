package com.litegateway.admin.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.alibaba.nacos.api.naming.pojo.Instance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 实例缓存管理器
 * 使用 Caffeine 实现 L1 本地缓存
 */
@Slf4j
@Component
public class InstanceCacheManager {

    /**
     * 服务实例列表缓存
     * key: serviceName, value: List<Instance>
     */
    private final Cache<String, List<Instance>> instanceListCache;
    /**
     * 服务列表缓存
     */
    private final Cache<String, List<String>> serviceListCache;

    public InstanceCacheManager() {
        this.instanceListCache = Caffeine.newBuilder()
                .maximumSize(2000)
                .expireAfterWrite(30, TimeUnit.SECONDS)
                .recordStats()
                .build();

        this.serviceListCache = Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(60, TimeUnit.SECONDS)
                .build();

        log.info("InstanceCacheManager initialized with Caffeine");
    }

    /**
     * 获取实例列表缓存
     */
    public List<Instance> getInstances(String serviceName) {
        return instanceListCache.getIfPresent(serviceName);
    }

    /**
     * 放入实例列表缓存
     */
    public void putInstances(String serviceName, List<Instance> instances) {
        instanceListCache.put(serviceName, instances);
    }

    /**
     * 获取服务列表缓存
     */
    public List<String> getServices(String key) {
        return serviceListCache.getIfPresent(key);
    }

    /**
     * 放入服务列表缓存
     */
    public void putServices(String key, List<String> services) {
        serviceListCache.put(key, services);
    }

    /**
     * 失效指定服务的缓存
     */
    public void invalidate(String serviceName) {
        instanceListCache.invalidate(serviceName);
        log.debug("Invalidated cache for service: {}", serviceName);
    }

    /**
     * 失效所有缓存
     */
    public void invalidateAll() {
        instanceListCache.invalidateAll();
        serviceListCache.invalidateAll();
        log.info("All caches invalidated");
    }

    /**
     * 获取缓存统计信息
     */
    public String getStats() {
        return String.format("Instance cache stats: %s", instanceListCache.stats());
    }
}
