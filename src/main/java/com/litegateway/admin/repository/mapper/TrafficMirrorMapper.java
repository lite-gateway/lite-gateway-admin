package com.litegateway.admin.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.litegateway.admin.repository.entity.TrafficMirror;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TrafficMirrorMapper extends BaseMapper<TrafficMirror> {

    @Select("SELECT * FROM traffic_mirror WHERE mirror_id = #{mirrorId}")
    TrafficMirror selectByMirrorId(@Param("mirrorId") String mirrorId);

    @Select("SELECT * FROM traffic_mirror WHERE enabled = 1")
    List<TrafficMirror> selectAllEnabled();

    @Select("<script>" +
            "SELECT * FROM traffic_mirror " +
            "<where>" +
            "<if test='mirrorName != null'> AND mirror_name LIKE CONCAT('%', #{mirrorName}, '%') </if>" +
            "<if test='sourceRouteId != null'> AND source_route_id = #{sourceRouteId} </if>" +
            "<if test='sourceServiceId != null'> AND source_service_id = #{sourceServiceId} </if>" +
            "<if test='enabled != null'> AND enabled = #{enabled} </if>" +
            "</where>" +
            "ORDER BY create_time DESC" +
            "</script>")
    IPage<TrafficMirror> selectByPage(Page<TrafficMirror> page,
                                      @Param("mirrorName") String mirrorName,
                                      @Param("sourceRouteId") String sourceRouteId,
                                      @Param("sourceServiceId") String sourceServiceId,
                                      @Param("enabled") Integer enabled);

    @Select("SELECT * FROM traffic_mirror WHERE source_route_id = #{routeId} AND enabled = 1")
    List<TrafficMirror> selectBySourceRouteId(@Param("routeId") String routeId);

    @Select("SELECT * FROM traffic_mirror WHERE source_service_id = #{serviceId} AND enabled = 1")
    List<TrafficMirror> selectBySourceServiceId(@Param("serviceId") String serviceId);
}
