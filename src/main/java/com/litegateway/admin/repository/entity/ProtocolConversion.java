package com.litegateway.admin.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("protocol_conversion")
public class ProtocolConversion {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String configId;
    private String configName;
    private String sourceProtocol;
    private String targetProtocol;
    private String protoFile;
    private String serviceName;
    private String methodName;
    private Integer status;
    private String description;

    @TableField(exist = false)
    private List<String> routeIds;

    private String createBy;
    private LocalDateTime createTime;
    private String updateBy;
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
