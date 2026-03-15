package com.litegateway.admin.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.litegateway.admin.repository.entity.LoadBalanceServiceRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 负载均衡配置与服务关联 Mapper 接口
 */
@Mapper
public interface LoadBalanceServiceRelationMapper extends BaseMapper<LoadBalanceServiceRelation> {

    /**
     * 根据配置ID查询关联的服务ID列表
     */
    @Select("SELECT service_id FROM load_balance_service_relation WHERE config_id = #{configId} AND deleted = 0")
    List<String> selectServiceIdsByConfigId(@Param("configId") String configId);

    /**
     * 根据服务ID查询关联的配置ID列表
     */
    @Select("SELECT config_id FROM load_balance_service_relation WHERE service_id = #{serviceId} AND deleted = 0")
    List<String> selectConfigIdsByServiceId(@Param("serviceId") String serviceId);

    /**
     * 根据配置ID删除所有关联
     */
    @Select("DELETE FROM load_balance_service_relation WHERE config_id = #{configId}")
    void deleteByConfigId(@Param("configId") String configId);

    /**
     * 根据服务ID删除所有关联
     */
    @Select("DELETE FROM load_balance_service_relation WHERE service_id = #{serviceId}")
    void deleteByServiceId(@Param("serviceId") String serviceId);
}
