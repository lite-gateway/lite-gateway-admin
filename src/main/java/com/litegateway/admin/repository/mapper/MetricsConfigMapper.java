package com.litegateway.admin.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.litegateway.admin.repository.entity.MetricsConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MetricsConfigMapper extends BaseMapper<MetricsConfig> {

    @Select("SELECT * FROM metrics_config WHERE status = 1 AND deleted = 0 LIMIT 1")
    MetricsConfig selectActiveConfig();

    @Select("SELECT * FROM metrics_config WHERE config_id = #{configId} AND deleted = 0")
    MetricsConfig selectByConfigId(@Param("configId") String configId);
}
