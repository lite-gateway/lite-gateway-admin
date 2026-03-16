package com.litegateway.admin.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.litegateway.admin.repository.entity.TraceRouteRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TraceRouteRelationMapper extends BaseMapper<TraceRouteRelation> {

    @Select("SELECT route_id FROM trace_route_relation WHERE config_id = #{configId} AND deleted = 0")
    List<String> selectRouteIdsByConfigId(@Param("configId") String configId);

    @Select("SELECT config_id FROM trace_route_relation WHERE route_id = #{routeId} AND deleted = 0")
    List<String> selectConfigIdsByRouteId(@Param("routeId") String routeId);

    @Select("DELETE FROM trace_route_relation WHERE config_id = #{configId}")
    void deleteByConfigId(@Param("configId") String configId);
}
