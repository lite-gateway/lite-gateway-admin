package com.litegateway.admin.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.litegateway.admin.repository.entity.ProtocolConversion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProtocolConversionMapper extends BaseMapper<ProtocolConversion> {

    @Select("SELECT * FROM protocol_conversion WHERE conversion_id = #{conversionId}")
    ProtocolConversion selectByConversionId(@Param("conversionId") String conversionId);

    @Select("SELECT * FROM protocol_conversion WHERE enabled = 1")
    List<ProtocolConversion> selectAllEnabled();

    @Select("<script>" +
            "SELECT * FROM protocol_conversion " +
            "<where>" +
            "<if test='name != null'> AND name LIKE CONCAT('%', #{name}, '%') </if>" +
            "<if test='sourceProtocol != null'> AND source_protocol = #{sourceProtocol} </if>" +
            "<if test='targetProtocol != null'> AND target_protocol = #{targetProtocol} </if>" +
            "<if test='enabled != null'> AND enabled = #{enabled} </if>" +
            "</where>" +
            "ORDER BY create_time DESC" +
            "</script>")
    IPage<ProtocolConversion> selectByPage(Page<ProtocolConversion> page,
                                           @Param("name") String name,
                                           @Param("sourceProtocol") String sourceProtocol,
                                           @Param("targetProtocol") String targetProtocol,
                                           @Param("enabled") Integer enabled);

    @Select("SELECT * FROM protocol_conversion WHERE JSON_CONTAINS(route_ids, JSON_ARRAY(#{routeId})) AND enabled = 1")
    List<ProtocolConversion> selectByRouteId(@Param("routeId") String routeId);

    @Select("SELECT * FROM protocol_conversion WHERE JSON_CONTAINS(service_ids, JSON_ARRAY(#{serviceId})) AND enabled = 1")
    List<ProtocolConversion> selectByServiceId(@Param("serviceId") String serviceId);
}
