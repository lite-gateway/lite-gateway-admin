package com.litegateway.admin.config;

import com.litegateway.admin.websocket.ServiceChangeWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocket 配置
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private ServiceChangeWebSocketHandler serviceChangeWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(serviceChangeWebSocketHandler, "/ws/service-changes")
                .setAllowedOrigins("*"); // 生产环境应该限制具体的域名
    }
}
