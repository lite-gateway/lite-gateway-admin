package com.litegateway.admin.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.litegateway.admin.repository.entity.HealthCheckConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface HealthCheckConfigMapper extends BaseMapper<HealthCheckConfig> {

    @Select("SELECT * FROM health_check_config WHERE config_id = #{configId} AND deleted = 0")
    HealthCheckConfig selectByConfigId(@Param("configId") String configId);

    @Select("SELECT * FROM health_check_config WHERE status = 1 AND deleted = 0 ORDER BY create_time DESC")
    List<HealthCheckConfig> selectAllEnabled();
}
