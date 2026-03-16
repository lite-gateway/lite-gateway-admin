package com.litegateway.admin.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("trace_route_relation")
public class TraceRouteRelation {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String configId;
    private String routeId;
    private LocalDateTime createTime;

    @TableLogic
    private Integer deleted;
}
