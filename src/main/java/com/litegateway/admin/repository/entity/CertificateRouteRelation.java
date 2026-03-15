package com.litegateway.admin.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 证书与路由关联实体类
 * 对应数据库表 certificate_route_relation
 * 多对多关系表
 */
@Data
@TableName("certificate_route_relation")
public class CertificateRouteRelation {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 证书ID
     */
    private String certId;

    /**
     * 路由ID
     */
    private String routeId;

    /**
     * 绑定的域名
     */
    private String domain;

    private LocalDateTime createTime;

    @TableLogic
    private Integer deleted;
}
