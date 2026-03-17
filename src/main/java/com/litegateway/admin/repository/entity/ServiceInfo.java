package com.litegateway.admin.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 服务信息实体类
 * 从Nacos同步的服务元数据
 */
@Data
@TableName("service_info")
public class ServiceInfo {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 服务名（Nacos服务名）
     */
    private String serviceName;

    /**
     * 命名空间ID
     */
    private String namespaceId;

    /**
     * 分组名
     */
    private String groupName;

    /**
     * 显示名称
     */
    private String displayName;

    /**
     * 服务描述
     */
    private String description;

    /**
     * 服务协议：http/https/grpc
     */
    private String protocol;

    /**
     * 基础路径（如 /api）
     */
    private String basePath;

    /**
     * Swagger文档地址
     */
    private String swaggerUrl;

    /**
     * 实例数量
     */
    private Integer instanceCount;

    /**
     * 健康实例数
     */
    private Integer healthyInstanceCount;

    /**
     * API数量
     */
    private Integer apiCount;

    /**
     * 路由数量
     */
    private Integer routeCount;

    /**
     * 服务状态：0-离线 1-在线 2-部分离线
     */
    private Integer status;

    /**
     * 是否从Nacos同步
     */
    private Boolean syncedFromNacos;

    /**
     * 最后同步时间
     */
    private LocalDateTime lastSyncTime;

    /**
     * 元数据（JSON）
     */
    private String metadata;

    private String createBy;

    private LocalDateTime createTime;

    private String updateBy;

    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
