package com.litegateway.admin.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@TableName(value = "traffic_mirror", autoResultMap = true)
public class TrafficMirror {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String mirrorId;

    private String mirrorName;

    private String description;

    private String sourceRouteId;

    private String sourceServiceId;

    private String targetHost;

    private Integer targetPort;

    private String targetProtocol;

    private String targetPath;

    private Integer mirrorPercentage;

    private Integer mirrorCount;

    private String sampleMode;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, String> headerFilter;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> excludePaths;

    private Integer copyRequestBody;

    private Integer copyResponseBody;

    private Integer asyncMode;

    private Long timeout;

    private Integer enabled;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String createBy;

    private String updateBy;
}
