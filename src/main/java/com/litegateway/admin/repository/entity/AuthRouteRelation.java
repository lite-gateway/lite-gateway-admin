package com.litegateway.admin.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 鉴权配置与路由关联实体类
 * 对应数据库表 auth_route_relation
 * 多对多关系表
 */
@Data
@TableName("auth_route_relation")
public class AuthRouteRelation {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 鉴权配置ID
     */
    private String configId;

    /**
     * 路由ID
     */
    private String routeId;

    private LocalDateTime createTime;

    @TableLogic
    private Integer deleted;
}
