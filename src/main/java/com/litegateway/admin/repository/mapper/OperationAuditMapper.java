package com.litegateway.admin.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.litegateway.admin.repository.entity.OperationAudit;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 操作审计日志Mapper
 */
@Mapper
public interface OperationAuditMapper extends BaseMapper<OperationAudit> {

    /**
     * 统计操作类型分布
     */
    @Select("SELECT operation_type, COUNT(*) as count FROM operation_audit " +
            "WHERE created_at BETWEEN #{startTime} AND #{endTime} AND deleted = 0 " +
            "GROUP BY operation_type")
    List<Map<String, Object>> selectOperationTypeStats(@Param("startTime") LocalDateTime startTime,
                                                        @Param("endTime") LocalDateTime endTime);

    /**
     * 统计模块操作分布
     */
    @Select("SELECT module, COUNT(*) as count FROM operation_audit " +
            "WHERE created_at BETWEEN #{startTime} AND #{endTime} AND deleted = 0 " +
            "GROUP BY module")
    List<Map<String, Object>> selectModuleStats(@Param("startTime") LocalDateTime startTime,
                                                 @Param("endTime") LocalDateTime endTime);
}
