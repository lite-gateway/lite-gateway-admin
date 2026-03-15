package com.litegateway.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.litegateway.admin.repository.entity.Certificate;
import com.litegateway.admin.repository.entity.CertificateRouteRelation;
import com.litegateway.admin.repository.mapper.CertificateMapper;
import com.litegateway.admin.repository.mapper.CertificateRouteRelationMapper;
import com.litegateway.admin.service.CertificateService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 证书管理服务实现类
 */
@Service
public class CertificateServiceImpl extends ServiceImpl<CertificateMapper, Certificate>
        implements CertificateService {

    @Autowired
    private CertificateRouteRelationMapper relationMapper;

    @Override
    public Page<Certificate> queryPage(String certName, String domain, Integer status, int pageNum, int pageSize) {
        LambdaQueryWrapper<Certificate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Certificate::getDeleted, 0);

        if (StringUtils.isNotBlank(certName)) {
            wrapper.like(Certificate::getCertName, certName);
        }
        if (StringUtils.isNotBlank(domain)) {
            wrapper.like(Certificate::getDomains, domain);
        }
        if (status != null) {
            wrapper.eq(Certificate::getStatus, status);
        }

        wrapper.orderByDesc(Certificate::getCreateTime);

        Page<Certificate> page = new Page<>(pageNum, pageSize);
        Page<Certificate> resultPage = baseMapper.selectPage(page, wrapper);

        // 加载关联的路由ID
        resultPage.getRecords().forEach(cert -> {
            List<String> routeIds = relationMapper.selectRouteIdsByCertId(cert.getCertId());
            cert.setRouteIds(routeIds);
            // 更新证书状态（检查是否过期）
            updateCertStatus(cert);
        });

        return resultPage;
    }

    @Override
    public Certificate getByCertId(String certId) {
        Certificate cert = baseMapper.selectByCertId(certId);
        if (cert != null) {
            List<String> routeIds = relationMapper.selectRouteIdsByCertId(certId);
            cert.setRouteIds(routeIds);
            updateCertStatus(cert);
        }
        return cert;
    }

    @Override
    public List<Certificate> listAllEnabled() {
        List<Certificate> list = baseMapper.selectAllEnabled();
        list.forEach(this::updateCertStatus);
        return list;
    }

    @Override
    public List<Certificate> listByDomain(String domain) {
        List<Certificate> list = baseMapper.selectByDomain(domain);
        list.forEach(this::updateCertStatus);
        return list;
    }

    @Override
    public List<Certificate> listExpiringSoon(int days) {
        LocalDateTime expireTime = LocalDateTime.now().plus(days, ChronoUnit.DAYS);
        List<Certificate> list = baseMapper.selectExpiringSoon(expireTime);
        list.forEach(this::updateCertStatus);
        return list;
    }

    @Override
    public List<Certificate> listByRouteId(String routeId) {
        List<String> certIds = relationMapper.selectCertIdsByRouteId(routeId);
        if (certIds.isEmpty()) {
            return List.of();
        }
        List<Certificate> list = baseMapper.selectBatchIds(certIds.stream().map(Long::valueOf).collect(Collectors.toList()));
        list.forEach(this::updateCertStatus);
        return list;
    }

    @Override
    public List<String> getBoundRouteIds(String certId) {
        return relationMapper.selectRouteIdsByCertId(certId);
    }

    @Override
    @Transactional
    public void bindRoutes(String certId, List<String> routeIds, String domain) {
        // 先删除旧关联
        relationMapper.deleteByCertId(certId);

        // 建立新关联
        if (routeIds != null && !routeIds.isEmpty()) {
            List<CertificateRouteRelation> relations = routeIds.stream()
                    .map(routeId -> {
                        CertificateRouteRelation relation = new CertificateRouteRelation();
                        relation.setCertId(certId);
                        relation.setRouteId(routeId);
                        relation.setDomain(domain);
                        relation.setCreateTime(LocalDateTime.now());
                        return relation;
                    })
                    .collect(Collectors.toList());

            relations.forEach(relationMapper::insert);
        }
    }

    @Override
    @Transactional
    public void unbindRoute(String certId, String routeId) {
        LambdaQueryWrapper<CertificateRouteRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CertificateRouteRelation::getCertId, certId)
                .eq(CertificateRouteRelation::getRouteId, routeId);
        relationMapper.delete(wrapper);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        Certificate cert = new Certificate();
        cert.setId(id);
        cert.setStatus(status);
        baseMapper.updateById(cert);
    }

    @Override
    @Transactional
    public void updateAutoRenew(Long id, Integer autoRenew, Integer renewReminderDays) {
        Certificate cert = new Certificate();
        cert.setId(id);
        cert.setAutoRenew(autoRenew);
        if (renewReminderDays != null) {
            cert.setRenewReminderDays(renewReminderDays);
        }
        baseMapper.updateById(cert);
    }

    @Override
    public String generateCertId() {
        return "cert_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    @Override
    public Certificate parseCertificate(String certContent, String keyContent) {
        // 这里可以集成 BouncyCastle 等库来解析证书信息
        // 简化实现，实际项目中应该解析证书获取真实信息
        Certificate cert = new Certificate();
        cert.setCertContent(certContent);
        cert.setKeyContent(keyContent);
        // TODO: 使用证书解析库获取真实信息
        return cert;
    }

    /**
     * 更新证书状态（检查是否过期）
     */
    private void updateCertStatus(Certificate cert) {
        if (cert.getNotAfter() == null) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime notAfter = cert.getNotAfter();
        LocalDateTime reminderTime = notAfter.minus(cert.getRenewReminderDays() != null ? cert.getRenewReminderDays() : 30, ChronoUnit.DAYS);

        if (now.isAfter(notAfter)) {
            cert.setStatus(0); // 已过期
        } else if (now.isAfter(reminderTime)) {
            cert.setStatus(2); // 即将过期
        } else {
            cert.setStatus(1); // 有效
        }
    }

    @Override
    @Transactional
    public boolean save(Certificate entity) {
        // 如果没有设置 certId，自动生成
        if (StringUtils.isBlank(entity.getCertId())) {
            entity.setCertId(generateCertId());
        }

        // 设置默认值
        if (entity.getStatus() == null) {
            entity.setStatus(1);
        }
        if (entity.getRenewReminderDays() == null) {
            entity.setRenewReminderDays(30);
        }
        if (entity.getAutoRenew() == null) {
            entity.setAutoRenew(0);
        }

        boolean success = super.save(entity);

        // 保存关联关系
        if (success && entity.getRouteIds() != null && !entity.getRouteIds().isEmpty()) {
            bindRoutes(entity.getCertId(), entity.getRouteIds(), entity.getDomains());
        }

        return success;
    }

    @Override
    @Transactional
    public boolean updateById(Certificate entity) {
        boolean success = super.updateById(entity);

        // 更新关联关系
        if (success && entity.getRouteIds() != null) {
            bindRoutes(entity.getCertId(), entity.getRouteIds(), entity.getDomains());
        }

        return success;
    }
}
