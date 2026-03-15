package com.litegateway.admin.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 重试配置实体类
 * 对应数据库表 retry_config
 * 支持路由级别的重试策略配置
 */
@Data
@TableName("retry_config")
public class RetryConfig {

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
     * 重试次数
     */
    private Integer retryCount;

    /**
     * 重试间隔策略：fixed（固定间隔）、exponential（指数退避）
     */
    private String backoffStrategy;

    /**
     * 基础重试间隔（毫秒）
     */
    private Integer baseInterval;

    /**
     * 最大重试间隔（毫秒），用于指数退避
     */
    private Integer maxInterval;

    /**
     * 重试超时时间（毫秒）
     */
    private Integer retryTimeout;

    /**
     * 可重试的状态码列表（逗号分隔），如：500,502,503,504
     */
    private String retryableStatusCodes;

    /**
     * 可重试的异常类型列表（逗号分隔），如：ConnectException,TimeoutException
     */
    private String retryableExceptions;

    /**
     * 状态：1启用 0禁用
     */
    private Integer status;

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
