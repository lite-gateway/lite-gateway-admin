package com.litegateway.admin.constants;

/**
 * Redis 通道常量
 * 从旧项目迁移，包名从 com.jtyjy.gateway 改为 com.litegateway
 */
public class RedisTypeConstants {

    public static final String CHANNEL = "lite:gateway:sync:route:update";
    public static final String IP_UPDATE = "ip_update";
    public static final String ROUTE_UPDATE = "route_update";
    public static final String WHITE_LIST_UPDATE = "white_list_update";
    
    // 限流规则相关消息类型
    public static final String RATE_LIMIT_UPDATE = "rate_limit_update";
    public static final String RATE_LIMIT_ROUTE_RELATION_UPDATE = "rate_limit_route_relation_update";
}
