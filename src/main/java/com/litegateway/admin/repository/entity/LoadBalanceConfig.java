package com.litegateway.admin.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 负载均衡配置实体类
 * 对应数据库表 load_balance_config
 * 支持多种负载均衡算法：轮询、随机、加权、最少连接、一致性哈希
 */
@Data
@TableName("load_balance_config")
public class LoadBalanceConfig {

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
     * 负载均衡策略：round_robin、random、weighted_round_robin、least_connections、consistent_hash
     */
    private String strategy;

    /**
     * 策略配置（JSON格式，根据strategy存储不同配置）
     * consistent_hash: {hashKey: "header|query|ip", keyName: "xxx"}
     * weighted: {weights: {"instanceId1": 10, "instanceId2": 20}}
     */
    private String strategyConfig;

    /**
     * 状态：1启用 0禁用
     */
    private Integer status;

    /**
     * 描述
     */
    private String description;

    /**
     * 关联的服务ID列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<String> serviceIds;

    private String createBy;

    private LocalDateTime createTime;

    private String updateBy;

    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
