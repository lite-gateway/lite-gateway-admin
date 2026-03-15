package com.litegateway.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.litegateway.admin.repository.entity.Certificate;

import java.util.List;

/**
 * 证书管理服务接口
 */
public interface CertificateService extends IService<Certificate> {

    /**
     * 分页查询证书
     */
    Page<Certificate> queryPage(String certName, String domain, Integer status, int pageNum, int pageSize);

    /**
     * 根据证书ID查询
     */
    Certificate getByCertId(String certId);

    /**
     * 查询所有启用的证书
     */
    List<Certificate> listAllEnabled();

    /**
     * 根据域名查询证书
     */
    List<Certificate> listByDomain(String domain);

    /**
     * 查询即将过期的证书
     */
    List<Certificate> listExpiringSoon(int days);

    /**
     * 根据路由ID查询关联的证书
     */
    List<Certificate> listByRouteId(String routeId);

    /**
     * 获取证书已绑定的路由ID列表
     */
    List<String> getBoundRouteIds(String certId);

    /**
     * 绑定路由到证书
     */
    void bindRoutes(String certId, List<String> routeIds, String domain);

    /**
     * 解绑路由从证书
     */
    void unbindRoute(String certId, String routeId);

    /**
     * 更新证书状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 更新自动续期配置
     */
    void updateAutoRenew(Long id, Integer autoRenew, Integer renewReminderDays);

    /**
     * 生成唯一的证书ID
     */
    String generateCertId();

    /**
     * 解析证书信息
     */
    Certificate parseCertificate(String certContent, String keyContent);
}
