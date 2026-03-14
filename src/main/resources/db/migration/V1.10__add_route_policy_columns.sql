-- 为 gateway_route 表添加服务关联和策略关联字段
-- V1.10

ALTER TABLE gateway_route
    ADD COLUMN service_id BIGINT(20) NULL COMMENT '关联服务ID' AFTER status,
    ADD COLUMN circuit_breaker_rule_id VARCHAR(64) NULL COMMENT '关联熔断规则ID' AFTER service_id,
    ADD COLUMN canary_rule_id VARCHAR(64) NULL COMMENT '关联灰度规则ID' AFTER circuit_breaker_rule_id;

-- 添加索引
ALTER TABLE gateway_route
    ADD INDEX idx_service_id (`service_id`),
    ADD INDEX idx_circuit_breaker_rule_id (`circuit_breaker_rule_id`),
    ADD INDEX idx_canary_rule_id (`canary_rule_id`);
