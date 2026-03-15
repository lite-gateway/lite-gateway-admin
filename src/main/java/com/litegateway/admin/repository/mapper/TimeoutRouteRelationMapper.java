package com.litegateway.admin.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.litegateway.admin.repository.entity.TimeoutRouteRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 超时配置与路由关联 Mapper 接口
 */
@Mapper
public interface TimeoutRouteRelationMapper extends BaseMapper<TimeoutRouteRelation> {

    /**
     * 根据配置ID查询关联的路由ID列表
     */
    @Select("SELECT route_id FROM timeout_route_relation WHERE config_id = #{configId} AND deleted = 0")
    List<String> selectRouteIdsByConfigId(@Param("configId") String configId);

    /**
     * 根据路由ID查询关联的配置ID列表
     */
    @Select("SELECT config_id FROM timeout_route_relation WHERE route_id = #{routeId} AND deleted = 0")
    List<String> selectConfigIdsByRouteId(@Param("routeId") String routeId);

    /**
     * 根据配置ID删除所有关联
     */
    @Select("DELETE FROM timeout_route_relation WHERE config_id = #{configId}")
    void deleteByConfigId(@Param("configId") String configId);

    /**
     * 根据路由ID删除所有关联
     */
    @Select("DELETE FROM timeout_route_relation WHERE route_id = #{routeId}")
    void deleteByRouteId(@Param("routeId") String routeId);
}
