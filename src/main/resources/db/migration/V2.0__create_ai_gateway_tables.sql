-- ============================================
-- AI Gateway 模块数据库表结构
-- 版本: 2.0
-- ============================================

-- 1. AI提供商表
CREATE TABLE ai_provider (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    provider_code VARCHAR(64) NOT NULL COMMENT '提供商编码(openai/azure/anthropic/groq等)',
    provider_name VARCHAR(128) NOT NULL COMMENT '提供商名称',
    provider_type VARCHAR(32) NOT NULL COMMENT '类型: cloud/local/proxy',
    
    -- 连接配置
    base_url VARCHAR(512) NOT NULL COMMENT '基础URL',
    api_key_encrypted VARCHAR(512) COMMENT '加密的API Key',
    api_version VARCHAR(32) COMMENT 'API版本(如2024-02-01)',
    
    -- 能力配置
    capabilities JSON COMMENT '支持的能力[chat/completion/embedding/vision]',
    protocol_type VARCHAR(32) DEFAULT 'openai' COMMENT '协议类型: openai/anthropic/ollama',
    
    -- 性能配置
    timeout_ms INT DEFAULT 30000 COMMENT '超时时间(毫秒)',
    max_retries INT DEFAULT 3 COMMENT '最大重试次数',
    concurrent_limit INT DEFAULT 100 COMMENT '并发限制',
    
    -- 状态管理
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    priority INT DEFAULT 100 COMMENT '优先级(数字越小优先级越高)',
    weight INT DEFAULT 100 COMMENT '权重(用于负载均衡)',
    
    -- 健康检查
    health_check_enabled TINYINT DEFAULT 1 COMMENT '是否启用健康检查',
    health_check_url VARCHAR(512) COMMENT '健康检查URL',
    last_health_check_time TIMESTAMP COMMENT '最后健康检查时间',
    health_status VARCHAR(32) DEFAULT 'unknown' COMMENT '健康状态',
    
    -- 元数据
    description VARCHAR(512) COMMENT '描述',
    tags VARCHAR(256) COMMENT '标签',
    extra_config JSON COMMENT '扩展配置',
    
    -- 审计字段
    created_by VARCHAR(64) COMMENT '创建人',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(64) COMMENT '更新人',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除',
    
    UNIQUE KEY uk_provider_code (provider_code),
    INDEX idx_status_priority (status, priority)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI提供商配置表';

-- 2. AI模型表
CREATE TABLE ai_model (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    provider_id BIGINT NOT NULL COMMENT '提供商ID',
    model_key VARCHAR(128) NOT NULL COMMENT '模型标识(gpt-4/claude-3-opus等)',
    model_name VARCHAR(128) NOT NULL COMMENT '模型显示名称',
    
    -- 模型特性
    model_type VARCHAR(32) DEFAULT 'chat' COMMENT '模型类型: chat/completion/embedding/vision',
    context_window INT COMMENT '上下文窗口大小',
    max_tokens INT COMMENT '最大输出token数',
    supports_streaming TINYINT DEFAULT 1 COMMENT '是否支持流式',
    supports_vision TINYINT DEFAULT 0 COMMENT '是否支持视觉',
    supports_tools TINYINT DEFAULT 0 COMMENT '是否支持工具调用',
    
    -- 价格配置 (每1K tokens)
    input_price DECIMAL(10,6) COMMENT '输入价格(USD)',
    output_price DECIMAL(10,6) COMMENT '输出价格(USD)',
    currency VARCHAR(3) DEFAULT 'USD' COMMENT '货币单位',
    
    -- 分组管理
    group_id BIGINT COMMENT '所属模型组ID',
    
    -- 状态管理
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    is_default TINYINT DEFAULT 0 COMMENT '是否为默认模型',
    display_order INT DEFAULT 0 COMMENT '显示顺序',
    
    -- 元数据
    description VARCHAR(512) COMMENT '描述',
    tags VARCHAR(256) COMMENT '标签',
    extra_config JSON COMMENT '扩展配置',
    
    -- 审计字段
    created_by VARCHAR(64) COMMENT '创建人',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(64) COMMENT '更新人',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除',
    
    UNIQUE KEY uk_provider_model (provider_id, model_key),
    INDEX idx_status_group (status, group_id),
    FOREIGN KEY (provider_id) REFERENCES ai_provider(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI模型配置表';

-- 3. 模型组表
CREATE TABLE ai_model_group (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    group_code VARCHAR(64) NOT NULL COMMENT '组编码',
    group_name VARCHAR(128) NOT NULL COMMENT '组名称',
    
    -- 分组策略
    routing_strategy VARCHAR(32) DEFAULT 'priority' COMMENT '路由策略: priority/cost/performance/round_robin',
    
    -- 状态
    status TINYINT DEFAULT 1 COMMENT '状态',
    description VARCHAR(512) COMMENT '描述',
    
    -- 审计字段
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    UNIQUE KEY uk_group_code (group_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI模型组表';

-- 4. 路由规则表
CREATE TABLE ai_route_rule (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    rule_name VARCHAR(128) NOT NULL COMMENT '规则名称',
    rule_code VARCHAR(64) NOT NULL COMMENT '规则编码',
    
    -- 匹配条件
    model_pattern VARCHAR(128) COMMENT '模型匹配模式(支持通配符)',
    request_headers JSON COMMENT '请求头匹配条件',
    request_params JSON COMMENT '请求参数匹配条件',
    
    -- 路由策略
    strategy_type VARCHAR(32) NOT NULL COMMENT '策略类型: priority/weighted/cost/latency',
    target_providers JSON NOT NULL COMMENT '目标提供商列表[provider_id1, provider_id2]',
    weights JSON COMMENT '权重配置{"provider_id": weight}',
    
    -- 条件路由
    conditions JSON COMMENT '条件表达式',
    
    -- 优先级和状态
    priority INT DEFAULT 100 COMMENT '优先级(数字越小越优先)',
    status TINYINT DEFAULT 1 COMMENT '状态',
    
    -- 审计字段
    created_by VARCHAR(64) COMMENT '创建人',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(64) COMMENT '更新人',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除',
    
    UNIQUE KEY uk_rule_code (rule_code),
    INDEX idx_priority_status (priority, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI路由规则表';

-- 5. 故障转移配置表
CREATE TABLE ai_fallback_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    rule_id BIGINT NOT NULL COMMENT '关联路由规则ID',
    
    -- 故障检测
    failure_threshold INT DEFAULT 3 COMMENT '故障阈值(连续失败次数)',
    failure_window_ms INT DEFAULT 60000 COMMENT '故障检测窗口(毫秒)',
    
    -- 故障转移策略
    fallback_type VARCHAR(32) DEFAULT 'next_provider' COMMENT '降级类型: next_provider/same_provider_retry/fallback_model',
    fallback_model_id BIGINT COMMENT '降级模型ID',
    retry_count INT DEFAULT 3 COMMENT '重试次数',
    retry_interval_ms INT DEFAULT 1000 COMMENT '重试间隔(毫秒)',
    
    -- 熔断配置
    circuit_breaker_enabled TINYINT DEFAULT 1 COMMENT '是否启用熔断',
    circuit_breaker_threshold INT DEFAULT 50 COMMENT '熔断阈值(错误率%)',
    circuit_breaker_duration_ms INT DEFAULT 30000 COMMENT '熔断持续时间(毫秒)',
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    FOREIGN KEY (rule_id) REFERENCES ai_route_rule(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='故障转移配置表';

-- 6. AI Agent表
CREATE TABLE ai_agent (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    agent_id VARCHAR(64) NOT NULL COMMENT 'Agent唯一标识',
    agent_name VARCHAR(128) NOT NULL COMMENT 'Agent名称',
    agent_type VARCHAR(32) DEFAULT 'personal' COMMENT '类型: personal/team/service',
    
    -- 所属关系
    owner_id VARCHAR(64) NOT NULL COMMENT '所属用户ID',
    team_id VARCHAR(64) COMMENT '所属团队ID',
    
    -- 配额管理
    daily_token_quota BIGINT DEFAULT 100000 COMMENT '每日Token配额',
    daily_cost_quota DECIMAL(10,4) DEFAULT 10.00 COMMENT '每日成本配额(USD)',
    monthly_cost_quota DECIMAL(10,4) DEFAULT 100.00 COMMENT '每月成本配额(USD)',
    
    -- 权限配置
    allowed_models JSON COMMENT '允许的模型列表',
    allowed_providers JSON COMMENT '允许的提供商列表',
    rate_limit_rps INT DEFAULT 10 COMMENT '每秒请求限制',
    rate_limit_rpm INT DEFAULT 60 COMMENT '每分钟请求限制',
    
    -- 状态
    status TINYINT DEFAULT 1 COMMENT '状态',
    api_key_hash VARCHAR(256) COMMENT 'API Key哈希',
    
    -- 审计字段
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除',
    
    UNIQUE KEY uk_agent_id (agent_id),
    INDEX idx_owner (owner_id, agent_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI Agent表';

-- 7. AI调用日志表
CREATE TABLE ai_call_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    
    -- 请求标识
    request_id VARCHAR(64) NOT NULL COMMENT '请求ID',
    trace_id VARCHAR(64) COMMENT '追踪ID',
    agent_id VARCHAR(64) COMMENT 'Agent ID',
    
    -- 路由信息
    provider_id BIGINT COMMENT '实际使用的提供商ID',
    model_id BIGINT COMMENT '实际使用的模型ID',
    requested_model VARCHAR(128) COMMENT '请求的模型',
    
    -- 用量统计
    input_tokens INT DEFAULT 0 COMMENT '输入token数',
    output_tokens INT DEFAULT 0 COMMENT '输出token数',
    total_tokens INT DEFAULT 0 COMMENT '总token数',
    
    -- 成本计算
    input_cost DECIMAL(10,6) COMMENT '输入成本',
    output_cost DECIMAL(10,6) COMMENT '输出成本',
    total_cost DECIMAL(10,6) COMMENT '总成本',
    currency VARCHAR(3) DEFAULT 'USD' COMMENT '货币',
    
    -- 性能指标
    latency_ms INT COMMENT '延迟(毫秒)',
    first_token_latency_ms INT COMMENT '首token延迟',
    
    -- 请求详情
    request_headers JSON COMMENT '请求头',
    request_body_hash VARCHAR(64) COMMENT '请求体哈希',
    response_status INT COMMENT '响应状态码',
    response_body_hash VARCHAR(64) COMMENT '响应体哈希',
    error_message TEXT COMMENT '错误信息',
    
    -- 流式标识
    is_streaming TINYINT DEFAULT 0 COMMENT '是否流式',
    
    -- 时间戳
    request_time TIMESTAMP NOT NULL COMMENT '请求时间',
    response_time TIMESTAMP COMMENT '响应时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '记录时间',
    
    INDEX idx_request_time (request_time),
    INDEX idx_agent_id (agent_id),
    INDEX idx_trace_id (trace_id),
    INDEX idx_provider_model (provider_id, model_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI调用日志表'
PARTITION BY RANGE (UNIX_TIMESTAMP(request_time)) (
    PARTITION p202401 VALUES LESS THAN (UNIX_TIMESTAMP('2024-02-01')),
    PARTITION p202402 VALUES LESS THAN (UNIX_TIMESTAMP('2024-03-01')),
    PARTITION p202403 VALUES LESS THAN (UNIX_TIMESTAMP('2024-04-01')),
    PARTITION pfuture VALUES LESS THAN MAXVALUE
);

-- 8. 成本汇总表
CREATE TABLE ai_cost_summary (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    
    -- 维度
    summary_type VARCHAR(32) NOT NULL COMMENT '汇总类型: daily/hourly/agent/model/provider',
    summary_date DATE NOT NULL COMMENT '汇总日期',
    summary_hour INT COMMENT '汇总小时(仅hourly类型)',
    
    agent_id VARCHAR(64) COMMENT 'Agent ID(按Agent汇总时)',
    model_id BIGINT COMMENT '模型ID(按模型汇总时)',
    provider_id BIGINT COMMENT '提供商ID(按提供商汇总时)',
    
    -- 统计指标
    call_count INT DEFAULT 0 COMMENT '调用次数',
    total_input_tokens BIGINT DEFAULT 0 COMMENT '总输入token',
    total_output_tokens BIGINT DEFAULT 0 COMMENT '总输出token',
    total_tokens BIGINT DEFAULT 0 COMMENT '总token',
    total_cost DECIMAL(12,6) DEFAULT 0 COMMENT '总成本',
    
    -- 性能指标
    avg_latency_ms INT COMMENT '平均延迟',
    p50_latency_ms INT COMMENT 'P50延迟',
    p95_latency_ms INT COMMENT 'P95延迟',
    p99_latency_ms INT COMMENT 'P99延迟',
    error_count INT DEFAULT 0 COMMENT '错误次数',
    error_rate DECIMAL(5,2) COMMENT '错误率',
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    UNIQUE KEY uk_summary (summary_type, summary_date, summary_hour, agent_id, model_id, provider_id),
    INDEX idx_date_type (summary_date, summary_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成本汇总表';

-- 9. 全局配置表
CREATE TABLE ai_gateway_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    config_key VARCHAR(128) NOT NULL COMMENT '配置键',
    config_value TEXT COMMENT '配置值',
    config_type VARCHAR(32) DEFAULT 'string' COMMENT '类型: string/json/number/boolean',
    description VARCHAR(512) COMMENT '描述',
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    UNIQUE KEY uk_config_key (config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='全局配置表';

-- 初始化配置
INSERT INTO ai_gateway_config (config_key, config_value, config_type, description) VALUES
('ai.gateway.enabled', 'true', 'boolean', '是否启用AI网关'),
('ai.gateway.default_timeout_ms', '30000', 'number', '默认超时时间'),
('ai.gateway.max_request_size', '10485760', 'number', '最大请求大小(10MB)'),
('ai.gateway.log_retention_days', '30', 'number', '日志保留天数'),
('ai.gateway.cost_alert_threshold', '100.00', 'number', '成本告警阈值(USD)');

-- 10. 配置变更事件表
CREATE TABLE ai_config_event (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    event_type VARCHAR(64) NOT NULL COMMENT '事件类型: provider/model/route/config',
    event_action VARCHAR(32) NOT NULL COMMENT '操作: create/update/delete/enable/disable',
    entity_type VARCHAR(64) NOT NULL COMMENT '实体类型',
    entity_id BIGINT NOT NULL COMMENT '实体ID',
    
    -- 变更详情
    old_value JSON COMMENT '变更前值',
    new_value JSON COMMENT '变更后值',
    change_diff JSON COMMENT '差异对比',
    
    -- 操作人
    operator_id VARCHAR(64) COMMENT '操作人ID',
    operator_name VARCHAR(128) COMMENT '操作人名称',
    
    -- 状态
    publish_status VARCHAR(32) DEFAULT 'pending' COMMENT '发布状态: pending/published/failed',
    publish_time TIMESTAMP COMMENT '发布时间',
    error_message TEXT COMMENT '错误信息',
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    INDEX idx_entity (entity_type, entity_id),
    INDEX idx_publish_status (publish_status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='配置变更事件表';

-- 插入示例数据
-- OpenAI 提供商
INSERT INTO ai_provider (provider_code, provider_name, provider_type, base_url, 
    capabilities, protocol_type, status, priority, description) VALUES
('openai', 'OpenAI', 'cloud', 'https://api.openai.com',
    '["chat", "completion", "embedding", "vision"]', 'openai', 1, 10, 'OpenAI官方API');

-- Azure OpenAI 提供商
INSERT INTO ai_provider (provider_code, provider_name, provider_type, base_url, 
    capabilities, protocol_type, status, priority, description) VALUES
('azure-openai', 'Azure OpenAI', 'cloud', 'https://your-resource.openai.azure.com',
    '["chat", "completion", "embedding"]', 'openai', 1, 20, 'Azure OpenAI服务');

-- 示例模型
INSERT INTO ai_model (provider_id, model_key, model_name, model_type, 
    context_window, max_tokens, supports_streaming, supports_vision, supports_tools,
    input_price, output_price, status, is_default, description) 
SELECT 
    id, 'gpt-4', 'GPT-4', 'chat',
    8192, 4096, 1, 1, 1,
    0.03, 0.06, 1, 0, 'GPT-4模型'
FROM ai_provider WHERE provider_code = 'openai';

INSERT INTO ai_model (provider_id, model_key, model_name, model_type, 
    context_window, max_tokens, supports_streaming, supports_vision, supports_tools,
    input_price, output_price, status, is_default, description) 
SELECT 
    id, 'gpt-3.5-turbo', 'GPT-3.5 Turbo', 'chat',
    4096, 4096, 1, 0, 1,
    0.0005, 0.0015, 1, 1, 'GPT-3.5 Turbo模型'
FROM ai_provider WHERE provider_code = 'openai';
