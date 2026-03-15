package com.litegateway.admin.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * SSL/TLS 证书实体类
 * 对应数据库表 certificate
 * 管理 HTTPS 证书上传、域名绑定和自动续期
 */
@Data
@TableName("certificate")
public class Certificate {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 证书业务ID（唯一标识）
     */
    private String certId;

    /**
     * 证书名称
     */
    private String certName;

    /**
     * 证书类型：pem、pfx
     */
    private String certType;

    /**
     * 证书内容（PEM格式）
     */
    private String certContent;

    /**
     * 私钥内容（PEM格式）
     */
    private String keyContent;

    /**
     * PFX 证书密码
     */
    private String pfxPassword;

    /**
     * 证书序列号
     */
    private String serialNumber;

    /**
     * 证书颁发者
     */
    private String issuer;

    /**
     * 证书主题
     */
    private String subject;

    /**
     * 证书生效时间
     */
    private LocalDateTime notBefore;

    /**
     * 证书过期时间
     */
    private LocalDateTime notAfter;

    /**
     * 绑定的域名列表（逗号分隔）
     */
    private String domains;

    /**
     * 状态：1有效 0过期 2即将过期
     */
    private Integer status;

    /**
     * 自动续期：0否 1是
     */
    private Integer autoRenew;

    /**
     * 续期提醒天数（默认30天）
     */
    private Integer renewReminderDays;

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
