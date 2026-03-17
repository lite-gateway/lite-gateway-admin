package com.litegateway.admin.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.litegateway.admin.repository.entity.AuthExemptApi;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 免鉴权APIMapper
 */
@Mapper
public interface AuthExemptApiMapper extends BaseMapper<AuthExemptApi> {

    /**
     * 查询生效的免鉴权API列表
     */
    @Select("SELECT * FROM auth_exempt_api " +
            "WHERE status = 1 AND deleted = 0 " +
            "AND (expire_time IS NULL OR expire_time > NOW())")
    List<AuthExemptApi> selectActiveExemptApis();

    /**
     * 根据路径和方法查询
     */
    @Select("SELECT * FROM auth_exempt_api " +
            "WHERE api_path = #{path} AND (request_method = #{method} OR request_method = 'ALL') " +
            "AND status = 1 AND deleted = 0")
    AuthExemptApi selectByPathAndMethod(@Param("path") String path, @Param("method") String method);
}
