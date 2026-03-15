package com.litegateway.admin.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 负载均衡配置与服务关联实体类
 * 对应数据库表 load_balance_service_relation
 * 多对多关系表
 */
@Data
@TableName("load_balance_service_relation")
public class LoadBalanceServiceRelation {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 负载均衡配置ID
     */
    private String configId;

    /**
     * 服务ID
     */
    private String serviceId;

    private LocalDateTime createTime;

    @TableLogic
    private Integer deleted;
}
