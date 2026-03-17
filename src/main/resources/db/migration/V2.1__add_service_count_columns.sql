-- Add api_count and route_count columns to service_info table
-- V2.1__add_service_count_columns.sql

ALTER TABLE service_info ADD COLUMN api_count INT DEFAULT 0 COMMENT 'API数量' AFTER healthy_instance_count;
ALTER TABLE service_info ADD COLUMN route_count INT DEFAULT 0 COMMENT '路由数量' AFTER api_count;
