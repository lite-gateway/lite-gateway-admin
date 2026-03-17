package com.litegateway.admin.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.litegateway.admin.repository.entity.ApiInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * API信息 Mapper 接口
 */
@Mapper
public interface ApiInfoMapper extends BaseMapper<ApiInfo> {

    /**
     * 根据服务ID查询API列表
     */
    @Select("SELECT * FROM api_info WHERE service_id = #{serviceId} AND deleted = 0 ORDER BY path ASC")
    List<ApiInfo> selectByServiceId(@Param("serviceId") Long serviceId);

    /**
     * 根据服务名查询API列表
     */
    @Select("SELECT * FROM api_info WHERE service_name = #{serviceName} AND deleted = 0 ORDER BY path ASC")
    List<ApiInfo> selectByServiceName(@Param("serviceName") String serviceName);

    /**
     * 查询已发布的API列表
     */
    @Select("SELECT * FROM api_info WHERE status = 1 AND deleted = 0 ORDER BY path ASC")
    List<ApiInfo> selectPublished();

    /**
     * 根据路径和方法查询
     */
    @Select("SELECT * FROM api_info WHERE path = #{path} AND method = #{method} AND deleted = 0 LIMIT 1")
    ApiInfo selectByPathAndMethod(@Param("path") String path, @Param("method") String method);

    /**
     * 统计服务下的API数量
     */
    @Select("SELECT COUNT(*) FROM api_info WHERE service_id = #{serviceId} AND deleted = 0")
    Long selectCountByServiceId(@Param("serviceId") Long serviceId);
}
