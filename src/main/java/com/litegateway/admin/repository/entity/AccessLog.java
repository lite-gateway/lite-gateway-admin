package com.litegateway.admin.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 访问日志实体类
 * 对应数据库表 access_log
 */
@Data
@TableName("access_log")
public class AccessLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 路由ID
     */
    private String routeId;

    /**
     * 请求路径
     */
    private String path;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 客户端IP
     */
    private String clientIp;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 响应状态码
     */
    private Integer statusCode;

    /**
     * 请求时间
     */
    private LocalDateTime requestTime;

    /**
     * 响应时间
     */
    private LocalDateTime responseTime;

    /**
     * 请求耗时(ms)
     */
    private Integer duration;

    /**
     * 请求大小(bytes)
     */
    private Long requestSize;

    /**
     * 响应大小(bytes)
     */
    private Long responseSize;

    /**
     * User-Agent
     */
    private String userAgent;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
