package com.litegateway.admin.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.litegateway.admin.repository.entity.HealthCheckServiceRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface HealthCheckServiceRelationMapper extends BaseMapper<HealthCheckServiceRelation> {

    @Select("SELECT service_id FROM health_check_service_relation WHERE config_id = #{configId} AND deleted = 0")
    List<String> selectServiceIdsByConfigId(@Param("configId") String configId);

    @Select("SELECT config_id FROM health_check_service_relation WHERE service_id = #{serviceId} AND deleted = 0")
    List<String> selectConfigIdsByServiceId(@Param("serviceId") String serviceId);

    @Select("DELETE FROM health_check_service_relation WHERE config_id = #{configId}")
    void deleteByConfigId(@Param("configId") String configId);
}
