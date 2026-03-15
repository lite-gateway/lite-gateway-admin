package com.litegateway.admin.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.litegateway.admin.repository.entity.RetryConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 重试配置 Mapper 接口
 */
@Mapper
public interface RetryConfigMapper extends BaseMapper<RetryConfig> {

    /**
     * 根据配置ID查询
     */
    @Select("SELECT * FROM retry_config WHERE config_id = #{configId} AND deleted = 0")
    RetryConfig selectByConfigId(@Param("configId") String configId);

    /**
     * 查询所有启用的重试配置
     */
    @Select("SELECT * FROM retry_config WHERE status = 1 AND deleted = 0 ORDER BY create_time DESC")
    List<RetryConfig> selectAllEnabled();
}
