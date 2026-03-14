package com.litegateway.admin.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * API信息实体类
 * 对应数据库表 api_info
 */
@Data
@TableName("api_info")
public class ApiInfo {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * API路径
     */
    private String path;

    /**
     * HTTP方法：GET/POST/PUT/DELETE/PATCH
     */
    private String method;

    /**
     * 所属服务ID
     */
    private Long serviceId;

    /**
     * 所属服务名
     */
    private String serviceName;

    /**
     * API版本（如 v1, v2）
     */
    private String version;

    /**
     * API名称/标题
     */
    private String title;

    /**
     * API描述
     */
    private String description;

    /**
     * 状态：0-草稿 1-已发布 2-已下线
     */
    private Integer status;

    /**
     * 是否需要认证
     */
    private Boolean requireAuth;

    /**
     * 请求参数描述（JSON）
     */
    private String requestParams;

    /**
     * 响应参数描述（JSON）
     */
    private String responseParams;

    /**
     * 关联的路由ID
     */
    private Long routeId;

    /**
     * Swagger来源
     */
    private String swaggerSource;

    /**
     * 标签/分组
     */
    private String tags;

    private String createBy;

    private LocalDateTime createTime;

    private String updateBy;

    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
