package com.litegateway.admin.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.litegateway.admin.repository.entity.TimeoutConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 超时配置 Mapper 接口
 */
@Mapper
public interface TimeoutConfigMapper extends BaseMapper<TimeoutConfig> {

    /**
     * 根据配置ID查询
     */
    @Select("SELECT * FROM timeout_config WHERE config_id = #{configId} AND deleted = 0")
    TimeoutConfig selectByConfigId(@Param("configId") String configId);

    /**
     * 查询所有启用的超时配置
     */
    @Select("SELECT * FROM timeout_config WHERE status = 1 AND deleted = 0 ORDER BY create_time DESC")
    List<TimeoutConfig> selectAllEnabled();

    /**
     * 根据配置类型查询
     */
    @Select("SELECT * FROM timeout_config WHERE config_type = #{configType} AND status = 1 AND deleted = 0")
    List<TimeoutConfig> selectByConfigType(@Param("configType") String configType);

    /**
     * 查询全局配置
     */
    @Select("SELECT * FROM timeout_config WHERE config_type = 'global' AND status = 1 AND deleted = 0 LIMIT 1")
    TimeoutConfig selectGlobalConfig();
}
