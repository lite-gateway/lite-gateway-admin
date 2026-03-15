package com.litegateway.admin.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.litegateway.admin.repository.entity.LoadBalanceConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 负载均衡配置 Mapper 接口
 */
@Mapper
public interface LoadBalanceConfigMapper extends BaseMapper<LoadBalanceConfig> {

    /**
     * 根据配置ID查询
     */
    @Select("SELECT * FROM load_balance_config WHERE config_id = #{configId} AND deleted = 0")
    LoadBalanceConfig selectByConfigId(@Param("configId") String configId);

    /**
     * 查询所有启用的负载均衡配置
     */
    @Select("SELECT * FROM load_balance_config WHERE status = 1 AND deleted = 0 ORDER BY create_time DESC")
    List<LoadBalanceConfig> selectAllEnabled();

    /**
     * 根据策略类型查询
     */
    @Select("SELECT * FROM load_balance_config WHERE strategy = #{strategy} AND status = 1 AND deleted = 0")
    List<LoadBalanceConfig> selectByStrategy(@Param("strategy") String strategy);
}
