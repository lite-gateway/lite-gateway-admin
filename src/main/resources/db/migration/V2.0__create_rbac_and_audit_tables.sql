-- ============================================
-- RBAC权限管理 + 审计日志 + 其他功能表
-- 版本: 2.0
-- ============================================

-- 1. 角色表
CREATE TABLE sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    role_code VARCHAR(64) NOT NULL COMMENT '角色编码',
    role_name VARCHAR(128) NOT NULL COMMENT '角色名称',
    description VARCHAR(512) COMMENT '角色描述',
    data_scope VARCHAR(32) DEFAULT 'all' COMMENT '数据范围：all-全部，custom-自定义，dept-本部门，dept_and_child-本部门及子部门，self-仅本人',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    sort_order INT DEFAULT 0 COMMENT '排序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除',
    UNIQUE KEY uk_role_code (role_code),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 2. 权限表
CREATE TABLE sys_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    permission_code VARCHAR(128) NOT NULL COMMENT '权限编码',
    permission_name VARCHAR(128) NOT NULL COMMENT '权限名称',
    permission_type VARCHAR(32) NOT NULL COMMENT '权限类型：menu-菜单，button-按钮，api-接口',
    parent_id BIGINT DEFAULT 0 COMMENT '父权限ID',
    path VARCHAR(256) COMMENT '菜单路径',
    icon VARCHAR(64) COMMENT '菜单图标',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除',
    UNIQUE KEY uk_permission_code (permission_code),
    INDEX idx_parent_id (parent_id),
    INDEX idx_type_status (permission_type, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 3. 用户角色关联表
CREATE TABLE sys_user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_role (user_id, role_id),
    INDEX idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 4. 角色权限关联表
CREATE TABLE sys_role_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_role_permission (role_id, permission_id),
    INDEX idx_permission_id (permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 5. 操作审计日志表
CREATE TABLE operation_audit (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    module VARCHAR(64) NOT NULL COMMENT '操作模块',
    operation_type VARCHAR(32) NOT NULL COMMENT '操作类型：CREATE-创建，UPDATE-更新，DELETE-删除，QUERY-查询，LOGIN-登录，LOGOUT-登出',
    description VARCHAR(512) COMMENT '操作描述',
    request_method VARCHAR(16) COMMENT '请求方法',
    request_url VARCHAR(512) COMMENT '请求URL',
    request_params TEXT COMMENT '请求参数',
    response_data TEXT COMMENT '响应结果',
    user_id BIGINT COMMENT '操作人ID',
    username VARCHAR(64) COMMENT '操作人用户名',
    ip_address VARCHAR(64) COMMENT '操作IP',
    location VARCHAR(128) COMMENT '操作地点',
    status TINYINT DEFAULT 1 COMMENT '操作状态：0-失败，1-成功',
    error_msg TEXT COMMENT '错误信息',
    execution_time BIGINT COMMENT '执行时长(毫秒)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除',
    INDEX idx_module (module),
    INDEX idx_operation_type (operation_type),
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作审计日志表';

-- 6. 免鉴权API表
CREATE TABLE auth_exempt_api (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    api_path VARCHAR(256) NOT NULL COMMENT 'API路径',
    request_method VARCHAR(16) DEFAULT 'ALL' COMMENT '请求方法：GET/POST/PUT/DELETE/ALL',
    description VARCHAR(512) COMMENT '描述',
    scope VARCHAR(32) DEFAULT 'global' COMMENT '生效范围：global-全局，tenant-租户',
    tenant_id BIGINT COMMENT '租户ID（scope为tenant时生效）',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    expire_time TIMESTAMP COMMENT '过期时间（null表示永不过期）',
    created_by VARCHAR(64) COMMENT '创建人',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除',
    INDEX idx_api_path (api_path),
    INDEX idx_scope (scope),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='免鉴权API表';

-- 7. WAF规则表
CREATE TABLE waf_rule (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    rule_name VARCHAR(128) NOT NULL COMMENT '规则名称',
    rule_type VARCHAR(64) NOT NULL COMMENT '规则类型：sql_injection-SQL注入，xss-XSS攻击，csrf-CSRF攻击，path_traversal-路径遍历，file_upload-文件上传，bot-恶意爬虫，custom-自定义',
    match_mode VARCHAR(32) NOT NULL COMMENT '匹配模式：regex-正则，keyword-关键字，path-路径，ip-IP地址',
    match_content TEXT NOT NULL COMMENT '匹配内容',
    risk_level VARCHAR(16) DEFAULT 'medium' COMMENT '风险等级：low-低，medium-中，high-高，critical-严重',
    action VARCHAR(32) DEFAULT 'block' COMMENT '动作：block-拦截，log-记录，captcha-验证码，rate_limit-限流',
    block_status_code INT DEFAULT 403 COMMENT '拦截状态码',
    block_response TEXT COMMENT '拦截响应内容',
    description VARCHAR(512) COMMENT '规则描述',
    priority INT DEFAULT 100 COMMENT '优先级',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    hit_count BIGINT DEFAULT 0 COMMENT '命中次数',
    created_by VARCHAR(64) COMMENT '创建人',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除',
    INDEX idx_rule_type (rule_type),
    INDEX idx_status (status),
    INDEX idx_priority (priority)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='WAF规则表';

-- 8. 策略模板表
CREATE TABLE policy_template (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    template_name VARCHAR(128) NOT NULL COMMENT '模板名称',
    template_code VARCHAR(64) NOT NULL COMMENT '模板编码',
    policy_type VARCHAR(64) NOT NULL COMMENT '策略类型：rate_limit-限流，circuit_breaker-熔断，retry-重试，canary-灰度，load_balance-负载均衡，timeout-超时',
    description VARCHAR(512) COMMENT '模板描述',
    policy_config JSON NOT NULL COMMENT '策略配置(JSON)',
    applicable_scenarios VARCHAR(512) COMMENT '适用场景',
    is_system TINYINT DEFAULT 0 COMMENT '是否系统预设：0-否，1-是',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    usage_count BIGINT DEFAULT 0 COMMENT '使用次数',
    created_by VARCHAR(64) COMMENT '创建人',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除',
    UNIQUE KEY uk_template_code (template_code),
    INDEX idx_policy_type (policy_type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='策略模板表';

-- 初始化权限数据
INSERT INTO sys_permission (permission_code, permission_name, permission_type, parent_id, path, icon, sort_order) VALUES
-- 仪表盘
('dashboard:view', '查看仪表盘', 'menu', 0, '/dashboard', 'DashboardOutlined', 1),

-- 流量引擎
('traffic:manage', '流量引擎', 'menu', 0, '/traffic', 'GatewayOutlined', 10),
('route:list', '路由列表', 'menu', 2, '/traffic/route', '', 1),
('route:create', '创建路由', 'button', 2, '', '', 2),
('route:update', '更新路由', 'button', 2, '', '', 3),
('route:delete', '删除路由', 'button', 2, '', '', 4),
('service:list', '服务列表', 'menu', 2, '/traffic/service', '', 5),
('rate_limit:manage', '限流策略', 'menu', 2, '/traffic/rate-limit', '', 6),
('circuit_breaker:manage', '熔断策略', 'menu', 2, '/traffic/circuit-breaker', '', 7),
('canary:manage', '灰度发布', 'menu', 2, '/traffic/canary', '', 8),
('policy_template:manage', '策略模板', 'menu', 2, '/traffic/policy-template', '', 9),

-- 安全中心
('security:manage', '安全中心', 'menu', 0, '/security', 'SecurityScanOutlined', 20),
('certificate:manage', 'TLS证书', 'menu', 11, '/security/certificate', '', 1),
('ip_blacklist:manage', 'IP黑名单', 'menu', 11, '/security/ip-blacklist', '', 2),
('whitelist:manage', 'IP白名单', 'menu', 11, '/security/whitelist', '', 3),
('auth_config:manage', 'API认证', 'menu', 11, '/security/auth-config', '', 4),
('auth_exempt:manage', '免鉴权API', 'menu', 11, '/security/auth-exempt', '', 5),
('waf:manage', 'WAF规则', 'menu', 11, '/security/waf', '', 6),

-- 可观测中心
('observability:manage', '可观测中心', 'menu', 0, '/observability', 'EyeOutlined', 30),
('log:view', '访问日志', 'menu', 18, '/observability/log', '', 1),
('metrics:view', '指标监控', 'menu', 18, '/observability/metrics', '', 2),
('trace:view', '链路追踪', 'menu', 18, '/observability/trace', '', 3),

-- 平台管理
('platform:manage', '平台管理', 'menu', 0, '/platform', 'SettingOutlined', 40),
('user:manage', '用户管理', 'menu', 22, '/platform/user', '', 1),
('role:manage', '角色权限', 'menu', 22, '/platform/role', '', 2),
('tenant:manage', '租户管理', 'menu', 22, '/platform/tenant', '', 3),
('audit:view', '操作审计', 'menu', 22, '/platform/audit', '', 4),
('system_config:manage', '系统配置', 'menu', 22, '/platform/config', '', 5);

-- 初始化角色数据
INSERT INTO sys_role (role_code, role_name, description, data_scope) VALUES
('super_admin', '超级管理员', '拥有所有权限', 'all'),
('admin', '管理员', '拥有大部分管理权限', 'all'),
('operator', '运维人员', '拥有流量管理和可观测权限', 'all'),
('viewer', '只读用户', '仅拥有查看权限', 'all');

-- 初始化策略模板数据
INSERT INTO policy_template (template_name, template_code, policy_type, description, policy_config, applicable_scenarios, is_system) VALUES
('默认限流模板', 'default_rate_limit', 'rate_limit', '适用于一般API的限流配置', '{"capacity": 100, "refillTokens": 10, "refillPeriod": 1}', '一般API保护', 1),
('严格限流模板', 'strict_rate_limit', 'rate_limit', '适用于敏感接口的严格限流', '{"capacity": 20, "refillTokens": 2, "refillPeriod": 1}', '登录、支付等敏感接口', 1),
('默认熔断模板', 'default_circuit_breaker', 'circuit_breaker', '适用于微服务的默认熔断配置', '{"failureRateThreshold": 50, "slowCallRateThreshold": 80, "slowCallDurationThreshold": 1000, "permittedNumberOfCallsInHalfOpenState": 10, "waitDurationInOpenState": 30}', '微服务保护', 1),
('默认重试模板', 'default_retry', 'retry', '适用于不稳定服务的重试配置', '{"maxAttempts": 3, "waitDuration": 1000, "retryExceptions": ["IOException", "TimeoutException"]}', '网络不稳定场景', 1);
