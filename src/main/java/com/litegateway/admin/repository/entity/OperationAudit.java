package com.litegateway.admin.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作审计日志实体
 */
@Data
@TableName("operation_audit")
public class OperationAudit {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 操作模块
     */
    private String module;

    /**
     * 操作类型：CREATE-创建，UPDATE-更新，DELETE-删除，QUERY-查询，LOGIN-登录，LOGOUT-登出
     */
    private String operationType;

    /**
     * 操作描述
     */
    private String description;

    /**
     * 请求方法
     */
    private String requestMethod;

    /**
     * 请求URL
     */
    private String requestUrl;

    /**
     * 请求参数
     */
    private String requestParams;

    /**
     * 响应结果
     */
    private String responseData;

    /**
     * 操作人ID
     */
    private Long userId;

    /**
     * 操作人用户名
     */
    private String username;

    /**
     * 操作IP
     */
    private String ipAddress;

    /**
     * 操作地点
     */
    private String location;

    /**
     * 操作状态：0-失败，1-成功
     */
    private Integer status;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 执行时长(毫秒)
     */
    private Long executionTime;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer deleted;
}
