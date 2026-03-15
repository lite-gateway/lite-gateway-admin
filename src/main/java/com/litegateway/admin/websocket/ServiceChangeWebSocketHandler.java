package com.litegateway.admin.websocket;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.litegateway.admin.websocket.message.ServiceChangeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 服务变更 WebSocket 处理器
 * 负责向前端推送服务实例变更事件
 */
@Slf4j
@Component
public class ServiceChangeWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 所有连接的会话
     */
    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

    /**
     * 服务订阅映射：sessionId -> 订阅的服务列表
     */
    private final Map<String, Set<String>> serviceSubscriptions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        serviceSubscriptions.put(session.getId(), ConcurrentHashMap.newKeySet());
        log.info("WebSocket connection established: {}, total sessions: {}", session.getId(), sessions.size());

        // 发送连接成功消息
        sendMessage(session, new ServiceChangeMessage("CONNECTED", null, null, "Connected to service change stream"));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        serviceSubscriptions.remove(session.getId());
        log.info("WebSocket connection closed: {}, status: {}, remaining sessions: {}",
                session.getId(), status, sessions.size());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.debug("Received WebSocket message from {}: {}", session.getId(), payload);

        try {
            // 解析客户端消息
            ServiceChangeMessage clientMessage = objectMapper.readValue(payload, ServiceChangeMessage.class);

            switch (clientMessage.getType()) {
                case "SUBSCRIBE":
                    // 客户端订阅特定服务
                    handleSubscribe(session, clientMessage.getServiceName());
                    break;
                case "UNSUBSCRIBE":
                    // 客户端取消订阅
                    handleUnsubscribe(session, clientMessage.getServiceName());
                    break;
                case "PING":
                    // 心跳响应
                    sendMessage(session, new ServiceChangeMessage("PONG", null, null, null));
                    break;
                default:
                    log.warn("Unknown message type: {}", clientMessage.getType());
            }
        } catch (Exception e) {
            log.error("Failed to handle WebSocket message", e);
            sendMessage(session, new ServiceChangeMessage("ERROR", null, null, "Invalid message format"));
        }
    }

    /**
     * 处理订阅请求
     */
    private void handleSubscribe(WebSocketSession session, String serviceName) {
        Set<String> subscriptions = serviceSubscriptions.get(session.getId());
        if (subscriptions != null && serviceName != null) {
            subscriptions.add(serviceName);
            log.info("Session {} subscribed to service: {}", session.getId(), serviceName);
            sendMessage(session, new ServiceChangeMessage("SUBSCRIBED", serviceName, null, null));
        }
    }

    /**
     * 处理取消订阅请求
     */
    private void handleUnsubscribe(WebSocketSession session, String serviceName) {
        Set<String> subscriptions = serviceSubscriptions.get(session.getId());
        if (subscriptions != null && serviceName != null) {
            subscriptions.remove(serviceName);
            log.info("Session {} unsubscribed from service: {}", session.getId(), serviceName);
            sendMessage(session, new ServiceChangeMessage("UNSUBSCRIBED", serviceName, null, null));
        }
    }

    /**
     * 广播服务变更事件给所有订阅的客户端
     */
    public void broadcastServiceChange(String serviceName, List<Instance> instances) {
        ServiceChangeMessage message = new ServiceChangeMessage(
                "SERVICE_CHANGE",
                serviceName,
                convertInstances(instances),
                null
        );

        String messageJson;
        try {
            messageJson = objectMapper.writeValueAsString(message);
        } catch (Exception e) {
            log.error("Failed to serialize service change message", e);
            return;
        }

        int sentCount = 0;
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                Set<String> subscriptions = serviceSubscriptions.get(session.getId());
                // 如果客户端订阅了该服务，或者订阅了所有服务（空集合表示订阅所有）
                if (subscriptions != null && (subscriptions.isEmpty() || subscriptions.contains(serviceName))) {
                    try {
                        session.sendMessage(new TextMessage(messageJson));
                        sentCount++;
                    } catch (IOException e) {
                        log.error("Failed to send message to session: {}", session.getId(), e);
                    }
                }
            }
        }

        log.debug("Broadcasted service change to {} clients: {}", sentCount, serviceName);
    }

    /**
     * 发送消息给指定会话
     */
    private void sendMessage(WebSocketSession session, ServiceChangeMessage message) {
        if (!session.isOpen()) {
            return;
        }

        try {
            String json = objectMapper.writeValueAsString(message);
            session.sendMessage(new TextMessage(json));
        } catch (Exception e) {
            log.error("Failed to send message to session: {}", session.getId(), e);
        }
    }

    /**
     * 转换 Instance 列表为 DTO 列表
     */
    private List<ServiceChangeMessage.InstanceDTO> convertInstances(List<Instance> instances) {
        if (instances == null) {
            return null;
        }
        return instances.stream()
                .map(this::convertInstance)
                .toList();
    }

    /**
     * 转换单个 Instance 为 DTO
     */
    private ServiceChangeMessage.InstanceDTO convertInstance(Instance instance) {
        ServiceChangeMessage.InstanceDTO dto = new ServiceChangeMessage.InstanceDTO();
        dto.setInstanceId(instance.getInstanceId());
        dto.setIp(instance.getIp());
        dto.setPort(instance.getPort());
        dto.setWeight(instance.getWeight());
        dto.setHealthy(instance.isHealthy());
        dto.setEnabled(instance.isEnabled());
        dto.setEphemeral(instance.isEphemeral());
        dto.setMetadata(instance.getMetadata());
        return dto;
    }

    /**
     * 获取当前连接数
     */
    public int getConnectionCount() {
        return sessions.size();
    }

    /**
     * 获取指定服务的订阅数
     */
    public int getSubscriptionCount(String serviceName) {
        int count = 0;
        for (Set<String> subscriptions : serviceSubscriptions.values()) {
            if (subscriptions.isEmpty() || subscriptions.contains(serviceName)) {
                count++;
            }
        }
        return count;
    }
}
