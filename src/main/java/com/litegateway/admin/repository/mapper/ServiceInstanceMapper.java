package com.litegateway.admin.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.litegateway.admin.repository.entity.ServiceInstance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 服务实例 Mapper 接口
 */
@Mapper
public interface ServiceInstanceMapper extends BaseMapper<ServiceInstance> {

    /**
     * 根据服务ID查询实例列表
     */
    @Select("SELECT * FROM service_instance WHERE service_id = #{serviceId} AND deleted = 0 ORDER BY create_time DESC")
    List<ServiceInstance> selectByServiceId(@Param("serviceId") Long serviceId);

    /**
     * 根据服务名查询实例列表
     */
    @Select("SELECT * FROM service_instance WHERE service_name = #{serviceName} AND deleted = 0 AND enabled = 1")
    List<ServiceInstance> selectByServiceName(@Param("serviceName") String serviceName);

    /**
     * 根据实例ID查询
     */
    @Select("SELECT * FROM service_instance WHERE instance_id = #{instanceId} AND deleted = 0")
    ServiceInstance selectByInstanceId(@Param("instanceId") String instanceId);

    /**
     * 更新实例权重
     */
    @Update("UPDATE service_instance SET weight = #{weight}, update_time = NOW() WHERE id = #{id}")
    int updateWeight(@Param("id") Long id, @Param("weight") Double weight);

    /**
     * 更新实例启用状态
     */
    @Update("UPDATE service_instance SET enabled = #{enabled}, update_time = NOW() WHERE id = #{id}")
    int updateEnabled(@Param("id") Long id, @Param("enabled") Boolean enabled);

    /**
     * 根据服务ID删除所有实例（用于全量同步）
     */
    @Update("UPDATE service_instance SET deleted = 1 WHERE service_id = #{serviceId}")
    int deleteByServiceId(@Param("serviceId") Long serviceId);

    /**
     * 统计服务的实例数
     */
    @Select("SELECT COUNT(*) FROM service_instance WHERE service_id = #{serviceId} AND deleted = 0")
    int countByServiceId(@Param("serviceId") Long serviceId);

    /**
     * 统计服务的健康实例数
     */
    @Select("SELECT COUNT(*) FROM service_instance WHERE service_id = #{serviceId} AND deleted = 0 AND healthy = 1")
    int countHealthyByServiceId(@Param("serviceId") Long serviceId);
}
