package com.litegateway.admin.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.litegateway.admin.repository.entity.TraceConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TraceConfigMapper extends BaseMapper<TraceConfig> {

    @Select("SELECT * FROM trace_config WHERE config_id = #{configId} AND deleted = 0")
    TraceConfig selectByConfigId(@Param("configId") String configId);

    @Select("SELECT * FROM trace_config WHERE status = 1 AND deleted = 0 ORDER BY create_time DESC")
    List<TraceConfig> selectAllEnabled();
}
