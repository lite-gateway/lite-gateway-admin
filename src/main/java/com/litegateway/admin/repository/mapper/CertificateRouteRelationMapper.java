package com.litegateway.admin.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.litegateway.admin.repository.entity.CertificateRouteRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 证书与路由关联 Mapper 接口
 */
@Mapper
public interface CertificateRouteRelationMapper extends BaseMapper<CertificateRouteRelation> {

    /**
     * 根据证书ID查询关联的路由ID列表
     */
    @Select("SELECT route_id FROM certificate_route_relation WHERE cert_id = #{certId} AND deleted = 0")
    List<String> selectRouteIdsByCertId(@Param("certId") String certId);

    /**
     * 根据路由ID查询关联的证书ID列表
     */
    @Select("SELECT cert_id FROM certificate_route_relation WHERE route_id = #{routeId} AND deleted = 0")
    List<String> selectCertIdsByRouteId(@Param("routeId") String routeId);

    /**
     * 根据证书ID删除所有关联
     */
    @Select("DELETE FROM certificate_route_relation WHERE cert_id = #{certId}")
    void deleteByCertId(@Param("certId") String certId);

    /**
     * 根据路由ID删除所有关联
     */
    @Select("DELETE FROM certificate_route_relation WHERE route_id = #{routeId}")
    void deleteByRouteId(@Param("routeId") String routeId);
}
