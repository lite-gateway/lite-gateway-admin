package com.litegateway.admin.event;

import org.springframework.context.ApplicationEvent;

/**
 * 功能配置变更事件
 */
public class FeatureConfigChangeEvent extends ApplicationEvent {

    private final String featureCode;

    public FeatureConfigChangeEvent(Object source, String featureCode) {
        super(source);
        this.featureCode = featureCode;
    }

    public String getFeatureCode() {
        return featureCode;
    }

    @Override
    public String toString() {
        return "FeatureConfigChangeEvent{" +
                "featureCode='" + featureCode + '\'' +
                ", source=" + getSource() +
                '}';
    }
}
