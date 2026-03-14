package com.litegateway.admin.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.litegateway.admin.repository.entity.GatewayFeatureConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 功能配置 Mapper 接口
 */
@Mapper
public interface GatewayFeatureConfigMapper extends BaseMapper<GatewayFeatureConfig> {

    /**
     * 根据功能编码查询
     */
    @Select("SELECT * FROM gateway_feature_config WHERE feature_code = #{featureCode} AND deleted = 0")
    GatewayFeatureConfig selectByFeatureCode(@Param("featureCode") String featureCode);

    /**
     * 查询所有启用的功能配置
     */
    @Select("SELECT * FROM gateway_feature_config WHERE enabled = 1 AND deleted = 0 ORDER BY priority ASC")
    List<GatewayFeatureConfig> selectAllEnabled();

    /**
     * 根据路由模式查询匹配的功能配置
     */
    @Select("SELECT * FROM gateway_feature_config WHERE enabled = 1 AND deleted = 0 " +
            "AND (route_patterns IS NULL OR route_patterns = '' OR route_patterns LIKE CONCAT('%', #{routeId}, '%')) " +
            "ORDER BY priority ASC")
    List<GatewayFeatureConfig> selectByRouteId(@Param("routeId") String routeId);
}
