package com.litegateway.admin.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.litegateway.admin.repository.entity.ServiceInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 服务信息 Mapper 接口
 */
@Mapper
public interface ServiceInfoMapper extends BaseMapper<ServiceInfo> {

    /**
     * 根据服务名查询
     */
    @Select("SELECT * FROM service_info WHERE service_name = #{serviceName} AND deleted = 0")
    ServiceInfo selectByServiceName(@Param("serviceName") String serviceName);

    /**
     * 查询所有在线服务
     */
    @Select("SELECT * FROM service_info WHERE status = 1 AND deleted = 0 ORDER BY create_time DESC")
    List<ServiceInfo> selectAllOnline();

    /**
     * 根据命名空间查询
     */
    @Select("SELECT * FROM service_info WHERE namespace_id = #{namespaceId} AND deleted = 0 ORDER BY create_time DESC")
    List<ServiceInfo> selectByNamespace(@Param("namespaceId") String namespaceId);

    /**
     * 更新实例数量
     */
    @Update("UPDATE service_info SET instance_count = #{count}, healthy_instance_count = #{healthyCount}, " +
            "update_time = NOW() WHERE id = #{id}")
    int updateInstanceCount(@Param("id") Long id, @Param("count") Integer count, @Param("healthyCount") Integer healthyCount);
}
