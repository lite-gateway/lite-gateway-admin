-- 初始化功能配置
INSERT INTO `gateway_feature_config` (`feature_code`, `feature_name`, `enabled`, `config_json`, `priority`, `route_patterns`, `description`) VALUES
('sensitive_data_masking', '敏感数据脱敏', 1, '{"fields":["password","phone","idCard","bankCard","email"],"rules":{"phone":"138****8888","idCard":"1101**********1234"}}', 10, NULL, '对请求和响应中的敏感数据进行脱敏处理'),
('ip_blacklist', 'IP黑名单', 1, '{}', 20, NULL, '拦截黑名单中的IP地址'),
('api_key_auth', 'API Key认证', 0, '{}', 30, NULL, '基于API Key的认证'),
('traffic_control', '流量控制', 1, '{}', 40, NULL, '本地流量控制'),
('traffic_coloring', '流量染色', 0, '{}', 50, NULL, '流量染色标记'),
('multi_tenant', '多租户隔离', 0, '{}', 60, NULL, '多租户请求隔离'),
('request_cache', '请求缓存', 0, '{}', 70, NULL, 'GET请求结果缓存'),
('response_cache', '响应缓存', 0, '{}', 80, NULL, '响应结果缓存'),
('blue_green', '蓝绿部署', 0, '{}', 90, NULL, '蓝绿发布支持'),
('canary', '金丝雀发布', 0, '{}', 100, NULL, '金丝雀灰度发布'),
('anomaly_detection', '异常检测', 0, '{}', 110, NULL, '流量异常检测'),
('request_mirror', '请求镜像', 0, '{}', 120, NULL, '请求流量镜像'),
('fine_grained_permission', '细粒度权限', 0, '{}', 130, NULL, '接口级权限控制'),
('access_log', '访问日志', 1, '{}', 140, NULL, '请求访问日志记录'),
('traffic_analysis', '流量分析', 1, '{}', 150, NULL, '实时流量分析统计'),
('websocket', 'WebSocket支持', 1, '{}', 160, NULL, 'WebSocket协议支持'),
('grpc', 'gRPC支持', 0, '{}', 170, NULL, 'gRPC协议支持'),
('graphql', 'GraphQL支持', 0, '{}', 180, NULL, 'GraphQL协议支持'),
('protocol_transform', '协议转换', 0, '{}', 190, NULL, '协议转换支持'),
('edge_computing', '边缘计算', 0, '{}', 200, NULL, '边缘脚本执行'),
('service_mesh', 'ServiceMesh', 0, '{}', 210, NULL, '服务网格集成'),
('conditional_routing', '条件路由', 0, '{}', 220, NULL, '基于条件的路由'),
('content_routing', '内容路由', 0, '{}', 230, NULL, '基于内容的路由'),
('request_rewrite', '请求重写', 0, '{}', 240, NULL, '请求重写'),
('waf', 'WAF防护', 0, '{}', 250, NULL, 'Web应用防火墙')
ON DUPLICATE KEY UPDATE `feature_name` = VALUES(`feature_name`);

-- 初始化熔断规则
INSERT INTO `circuit_breaker_rule` (`rule_id`, `rule_name`, `route_id`, `failure_rate_threshold`, `wait_duration_in_open_state`, `permitted_number_of_calls_in_half_open_state`, `sliding_window_size`, `minimum_number_of_calls`, `slow_call_rate_threshold`, `slow_call_duration_threshold`, `timeout_duration`, `enabled`) VALUES
('global_circuit_breaker', '全局熔断规则', NULL, 50, 60, 10, 100, 10, 50, 5, 5, 1)
ON DUPLICATE KEY UPDATE `rule_name` = VALUES(`rule_name`);
