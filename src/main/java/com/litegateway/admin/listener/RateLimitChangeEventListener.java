package com.litegateway.admin.listener;

import com.litegateway.admin.constants.RedisTypeConstants;
import com.litegateway.admin.event.RateLimitChangeEvent;
import com.litegateway.admin.event.RateLimitRouteRelationChangeEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 限流规则变更事件监听器
 * 监听限流规则变更事件，通过 Redis Pub/Sub 通知所有 Gateway Core 实例
 */
@Slf4j
@Component
public class RateLimitChangeEventListener {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 监听限流规则参数变更事件
     * 当限流规则的配置（如 replenishRate、burstCapacity 等）发生变更时触发
     */
    @EventListener
    public void onRateLimitChange(RateLimitChangeEvent event) {
        String ruleId = event.getRuleId();
        log.info("Received RateLimitChangeEvent for rule: {}, publishing to Redis", ruleId);

        try {
            // 发布限流规则变更消息到 Redis，通知所有 Gateway 实例
            redisTemplate.convertAndSend(RedisTypeConstants.CHANNEL, RedisTypeConstants.RATE_LIMIT_UPDATE);
            log.info("Published rate limit update message to Redis channel: {}", RedisTypeConstants.CHANNEL);
        } catch (Exception e) {
            log.error("Failed to publish rate limit update message to Redis", e);
        }
    }

    /**
     * 监听限流规则与路由关联变更事件
     * 当限流规则与路由的关联关系发生变更时触发（绑定/解绑）
     */
    @EventListener
    public void onRateLimitRelationChange(RateLimitRouteRelationChangeEvent event) {
        String routeId = event.getRouteId();
        String ruleId = event.getRuleId();
        RateLimitRouteRelationChangeEvent.ChangeType changeType = event.getChangeType();

        log.info("Received RateLimitRouteRelationChangeEvent: route={}, rule={}, type={}",
                routeId, ruleId, changeType);

        try {
            // 发布限流规则关联变更消息到 Redis，通知所有 Gateway 实例重建路由
            redisTemplate.convertAndSend(RedisTypeConstants.CHANNEL, 
                    RedisTypeConstants.RATE_LIMIT_ROUTE_RELATION_UPDATE);
            log.info("Published rate limit route relation update message to Redis channel: {}", 
                    RedisTypeConstants.CHANNEL);
        } catch (Exception e) {
            log.error("Failed to publish rate limit route relation update message to Redis", e);
        }
    }
}
