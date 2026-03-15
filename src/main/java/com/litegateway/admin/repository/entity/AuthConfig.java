package com.litegateway.admin.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 鉴权配置实体类
 * 对应数据库表 auth_config
 * 支持 JWT、OAuth2、API Key 等多种鉴权方式
 */
@Data
@TableName("auth_config")
public class AuthConfig {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 配置业务ID（唯一标识）
     */
    private String configId;

    /**
     * 配置名称
     */
    private String configName;

    /**
     * 鉴权类型：jwt、oauth2、apikey、aksk
     */
    private String authType;

    /**
     * 状态：1启用 0禁用
     */
    private Integer status;

    /**
     * 配置详情（JSON格式，根据authType存储不同配置）
     */
    private String configJson;

    /**
     * 描述
     */
    private String description;

    /**
     * 关联的路由ID列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<String> routeIds;

    private String createBy;

    private LocalDateTime createTime;

    private String updateBy;

    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
