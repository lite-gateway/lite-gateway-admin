package com.litegateway.admin.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.litegateway.admin.repository.entity.Tenant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TenantMapper extends BaseMapper<Tenant> {

    @Select("SELECT * FROM tenant WHERE tenant_id = #{tenantId}")
    Tenant selectByTenantId(@Param("tenantId") String tenantId);

    @Select("SELECT * FROM tenant WHERE tenant_code = #{tenantCode}")
    Tenant selectByTenantCode(@Param("tenantCode") String tenantCode);

    @Select("SELECT * FROM tenant WHERE status = 'ACTIVE'")
    List<Tenant> selectAllActive();

    @Select("<script>" +
            "SELECT * FROM tenant " +
            "<where>" +
            "<if test='tenantName != null'> AND tenant_name LIKE CONCAT('%', #{tenantName}, '%') </if>" +
            "<if test='tenantCode != null'> AND tenant_code LIKE CONCAT('%', #{tenantCode}, '%') </if>" +
            "<if test='status != null'> AND status = #{status} </if>" +
            "</where>" +
            "ORDER BY create_time DESC" +
            "</script>")
    IPage<Tenant> selectByPage(Page<Tenant> page,
                               @Param("tenantName") String tenantName,
                               @Param("tenantCode") String tenantCode,
                               @Param("status") String status);

    @Select("SELECT * FROM tenant WHERE JSON_CONTAINS(route_ids, JSON_ARRAY(#{routeId}))")
    List<Tenant> selectByRouteId(@Param("routeId") String routeId);

    @Select("SELECT * FROM tenant WHERE JSON_CONTAINS(service_ids, JSON_ARRAY(#{serviceId}))")
    List<Tenant> selectByServiceId(@Param("serviceId") String serviceId);
}
