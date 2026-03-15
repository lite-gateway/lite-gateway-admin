package com.litegateway.admin.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.litegateway.admin.repository.entity.AuthConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 鉴权配置 Mapper 接口
 */
@Mapper
public interface AuthConfigMapper extends BaseMapper<AuthConfig> {

    /**
     * 根据配置ID查询
     */
    @Select("SELECT * FROM auth_config WHERE config_id = #{configId} AND deleted = 0")
    AuthConfig selectByConfigId(@Param("configId") String configId);

    /**
     * 查询所有启用的鉴权配置
     */
    @Select("SELECT * FROM auth_config WHERE status = 1 AND deleted = 0 ORDER BY create_time DESC")
    List<AuthConfig> selectAllEnabled();

    /**
     * 根据鉴权类型查询
     */
    @Select("SELECT * FROM auth_config WHERE auth_type = #{authType} AND status = 1 AND deleted = 0")
    List<AuthConfig> selectByAuthType(@Param("authType") String authType);
}
