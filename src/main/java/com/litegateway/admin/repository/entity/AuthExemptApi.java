package com.litegateway.admin.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 免鉴权API实体
 */
@Data
@TableName("auth_exempt_api")
public class AuthExemptApi {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * API路径
     */
    private String apiPath;

    /**
     * 请求方法：GET/POST/PUT/DELETE/ALL
     */
    private String requestMethod;

    /**
     * 描述
     */
    private String description;

    /**
     * 生效范围：global-全局，tenant-租户
     */
    private String scope;

    /**
     * 租户ID（scope为tenant时生效）
     */
    private Long tenantId;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 过期时间（null表示永不过期）
     */
    private LocalDateTime expireTime;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer deleted;
}
