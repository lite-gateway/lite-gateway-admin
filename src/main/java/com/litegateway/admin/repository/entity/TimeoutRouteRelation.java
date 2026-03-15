package com.litegateway.admin.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 超时配置与路由关联实体类
 * 对应数据库表 timeout_route_relation
 * 多对多关系表
 */
@Data
@TableName("timeout_route_relation")
public class TimeoutRouteRelation {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 超时配置ID
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
