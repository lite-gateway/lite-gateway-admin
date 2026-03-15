package com.litegateway.admin.config;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Nacos 事件监听器
 * 用于处理 Nacos 服务变更事件
 */
@Slf4j
@Component
public class NacosEventListener {

    private volatile boolean active = true;

    public NacosEventListener() {
        log.info("NacosEventListener initialized");
    }

    /**
     * 检查监听器是否处于活动状态
     */
    public boolean isActive() {
        return active;
    }

    @PreDestroy
    public void destroy() {
        active = false;
        log.info("NacosEventListener destroyed");
    }
}
