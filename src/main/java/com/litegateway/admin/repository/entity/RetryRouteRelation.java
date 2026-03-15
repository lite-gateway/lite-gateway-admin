package com.litegateway.admin.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 重试配置与路由关联实体类
 * 对应数据库表 retry_route_relation
 * 多对多关系表
 */
@Data
@TableName("retry_route_relation")
public class RetryRouteRelation {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 重试配置ID
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
