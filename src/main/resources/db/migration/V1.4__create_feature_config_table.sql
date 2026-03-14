CREATE TABLE IF NOT EXISTS `gateway_feature_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `feature_code` varchar(64) NOT NULL COMMENT '功能编码',
  `feature_name` varchar(128) NOT NULL COMMENT '功能名称',
  `enabled` tinyint(1) DEFAULT '1' COMMENT '是否启用',
  `config_json` text COMMENT '功能配置JSON',
  `priority` int(11) DEFAULT '100' COMMENT '执行优先级',
  `route_patterns` varchar(512) DEFAULT NULL COMMENT '适用路由模式',
  `description` varchar(512) DEFAULT NULL COMMENT '描述',
  `create_by` varchar(64) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_by` varchar(64) DEFAULT NULL,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_feature_code` (`feature_code`),
  KEY `idx_enabled` (`enabled`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='网关功能配置表';
