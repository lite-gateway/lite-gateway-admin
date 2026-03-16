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
@TableName(value = "plugin", autoResultMap = true)
public class Plugin {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String pluginId;

    private String pluginName;

    private String description;

    private String pluginType;

    private String executePhase;

    private String luaScript;

    private String scriptPath;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> defaultConfig;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> routeIds;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> serviceIds;

    private Integer priority;

    private Long timeout;

    private Integer memoryLimit;

    private String version;

    private Integer enabled;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String createBy;

    private String updateBy;
}
