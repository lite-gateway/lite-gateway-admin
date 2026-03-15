package com.litegateway.admin.websocket.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 服务变更消息
 * 用于 WebSocket 通信
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceChangeMessage {

    /**
     * 消息类型
     * CONNECTED - 连接成功
     * SERVICE_CHANGE - 服务变更
     * SUBSCRIBED - 订阅成功
     * UNSUBSCRIBED - 取消订阅成功
     * PING/PONG - 心跳
     * ERROR - 错误
     */
    private String type;

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 实例列表
     */
    private List<InstanceDTO> instances;

    /**
     * 消息内容（用于错误信息或提示）
     */
    private String message;

    /**
     * 时间戳
     */
    private long timestamp = System.currentTimeMillis();

    public ServiceChangeMessage(String type, String serviceName, List<InstanceDTO> instances, String message) {
        this.type = type;
        this.serviceName = serviceName;
        this.instances = instances;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 实例 DTO
     */
    @Data
    @NoArgsConstructor
    public static class InstanceDTO {
        private String instanceId;
        private String ip;
        private int port;
        private double weight;
        private boolean healthy;
        private boolean enabled;
        private boolean ephemeral;
        private Map<String, String> metadata;
    }
}
