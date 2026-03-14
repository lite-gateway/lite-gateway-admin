package com.litegateway.admin.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 服务实例实体类
 * 从Nacos同步的实例信息
 */
@Data
@TableName("service_instance")
public class ServiceInstance {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联服务ID
     */
    private Long serviceId;

    /**
     * 服务名
     */
    private String serviceName;

    /**
     * 实例ID（Nacos实例唯一标识）
     */
    private String instanceId;

    /**
     * IP地址
     */
    private String ip;

    /**
     * 端口
     */
    private Integer port;

    /**
     * 权重
     */
    private Double weight;

    /**
     * 是否健康
     */
    private Boolean healthy;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 是否临时实例
     */
    private Boolean ephemeral;

    /**
     * 集群名
     */
    private String clusterName;

    /**
     * 实例元数据（JSON）
     */
    private String metadata;

    /**
     * 最后心跳时间
     */
    private LocalDateTime lastBeatTime;

    private String createBy;

    private LocalDateTime createTime;

    private String updateBy;

    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
