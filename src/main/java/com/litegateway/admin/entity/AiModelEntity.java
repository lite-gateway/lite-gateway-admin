package com.litegateway.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * AI模型实体
 */
@Data
@TableName("ai_model")
public class AiModelEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 提供商ID
     */
    private Long providerId;

    /**
     * 模型标识
     */
    private String modelKey;

    /**
     * 模型显示名称
     */
    private String modelName;

    /**
     * 模型类型
     */
    private String modelType;

    /**
     * 上下文窗口大小
     */
    private Integer contextWindow;

    /**
     * 最大输出token数
     */
    private Integer maxTokens;

    /**
     * 是否支持流式
     */
    private Integer supportsStreaming;

    /**
     * 是否支持视觉
     */
    private Integer supportsVision;

    /**
     * 是否支持工具调用
     */
    private Integer supportsTools;

    /**
     * 输入价格(每1K tokens)
     */
    private BigDecimal inputPrice;

    /**
     * 输出价格(每1K tokens)
     */
    private BigDecimal outputPrice;

    /**
     * 货币单位
     */
    private String currency;

    /**
     * 所属模型组ID
     */
    private Long groupId;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 是否为默认模型
     */
    private Integer isDefault;

    /**
     * 显示顺序
     */
    private Integer displayOrder;

    /**
     * 描述
     */
    private String description;

    /**
     * 标签
     */
    private String tags;

    /**
     * 扩展配置(JSON)
     */
    private String extraConfig;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新人
     */
    private String updatedBy;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer deleted;
}
