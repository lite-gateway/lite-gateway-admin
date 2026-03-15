package com.litegateway.admin.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.litegateway.admin.repository.entity.Certificate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 证书管理 Mapper 接口
 */
@Mapper
public interface CertificateMapper extends BaseMapper<Certificate> {

    /**
     * 根据证书ID查询
     */
    @Select("SELECT * FROM certificate WHERE cert_id = #{certId} AND deleted = 0")
    Certificate selectByCertId(@Param("certId") String certId);

    /**
     * 查询所有启用的证书
     */
    @Select("SELECT * FROM certificate WHERE status != 0 AND deleted = 0 ORDER BY create_time DESC")
    List<Certificate> selectAllEnabled();

    /**
     * 查询即将过期的证书
     */
    @Select("SELECT * FROM certificate WHERE not_after <= #{expireTime} AND status = 1 AND deleted = 0")
    List<Certificate> selectExpiringSoon(@Param("expireTime") LocalDateTime expireTime);

    /**
     * 根据域名查询证书
     */
    @Select("SELECT * FROM certificate WHERE domains LIKE CONCAT('%', #{domain}, '%') AND status != 0 AND deleted = 0")
    List<Certificate> selectByDomain(@Param("domain") String domain);
}
