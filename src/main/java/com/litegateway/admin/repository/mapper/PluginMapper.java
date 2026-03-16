package com.litegateway.admin.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.litegateway.admin.repository.entity.Plugin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PluginMapper extends BaseMapper<Plugin> {

    @Select("SELECT * FROM plugin WHERE plugin_id = #{pluginId}")
    Plugin selectByPluginId(@Param("pluginId") String pluginId);

    @Select("SELECT * FROM plugin WHERE enabled = 1 ORDER BY priority DESC, create_time DESC")
    List<Plugin> selectAllEnabled();

    @Select("<script>" +
            "SELECT * FROM plugin " +
            "<where>" +
            "<if test='pluginName != null'> AND plugin_name LIKE CONCAT('%', #{pluginName}, '%') </if>" +
            "<if test='pluginType != null'> AND plugin_type = #{pluginType} </if>" +
            "<if test='executePhase != null'> AND execute_phase = #{executePhase} </if>" +
            "<if test='enabled != null'> AND enabled = #{enabled} </if>" +
            "</where>" +
            "ORDER BY priority DESC, create_time DESC" +
            "</script>")
    IPage<Plugin> selectByPage(Page<Plugin> page,
                               @Param("pluginName") String pluginName,
                               @Param("pluginType") String pluginType,
                               @Param("executePhase") String executePhase,
                               @Param("enabled") Integer enabled);

    @Select("SELECT * FROM plugin WHERE JSON_CONTAINS(route_ids, JSON_ARRAY(#{routeId})) AND enabled = 1 ORDER BY priority DESC")
    List<Plugin> selectByRouteId(@Param("routeId") String routeId);

    @Select("SELECT * FROM plugin WHERE JSON_CONTAINS(service_ids, JSON_ARRAY(#{serviceId})) AND enabled = 1 ORDER BY priority DESC")
    List<Plugin> selectByServiceId(@Param("serviceId") String serviceId);
}
