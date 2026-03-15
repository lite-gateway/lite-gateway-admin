package com.litegateway.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.litegateway.admin.common.web.PageBody;
import com.litegateway.admin.common.web.Result;
import com.litegateway.admin.repository.entity.Certificate;
import com.litegateway.admin.service.CertificateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 证书管理控制器
 */
@RestController
@RequestMapping("/gateway/certificate")
@Tag(name = "证书管理", description = "SSL/TLS 证书上传、管理和自动续期")
public class CertificateController {

    @Autowired
    private CertificateService certificateService;

    /**
     * 分页查询证书
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询证书")
    public Result<PageBody<Certificate>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String certName,
            @RequestParam(required = false) String domain,
            @RequestParam(required = false) Integer status) {

        Page<Certificate> page = certificateService.queryPage(certName, domain, status, pageNum, pageSize);

        PageBody<Certificate> pageBody = new PageBody<>();
        pageBody.setList(page.getRecords());
        pageBody.setTotal(page.getTotal());
        pageBody.setPages((int) page.getPages());
        pageBody.setPageSize((int) page.getSize());
        pageBody.setPageNum((int) page.getCurrent());

        return Result.ok(pageBody);
    }

    /**
     * 查询所有启用的证书
     */
    @GetMapping("/list")
    @Operation(summary = "查询所有启用的证书")
    public Result<List<Certificate>> listAll() {
        List<Certificate> list = certificateService.listAllEnabled();
        return Result.ok(list);
    }

    /**
     * 查询即将过期的证书
     */
    @GetMapping("/expiring-soon")
    @Operation(summary = "查询即将过期的证书")
    public Result<List<Certificate>> listExpiringSoon(@RequestParam(defaultValue = "30") int days) {
        List<Certificate> list = certificateService.listExpiringSoon(days);
        return Result.ok(list);
    }

    /**
     * 根据ID查询证书
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询证书")
    public Result<Certificate> getById(@PathVariable Long id) {
        Certificate cert = certificateService.getById(id);
        if (cert != null) {
            List<String> routeIds = certificateService.getBoundRouteIds(cert.getCertId());
            cert.setRouteIds(routeIds);
        }
        return Result.ok(cert);
    }

    /**
     * 根据证书ID获取详情
     */
    @GetMapping("/cert/{certId}")
    @Operation(summary = "根据证书ID查询")
    public Result<Certificate> getByCertId(@PathVariable String certId) {
        Certificate cert = certificateService.getByCertId(certId);
        return Result.ok(cert);
    }

    /**
     * 新增证书
     */
    @PostMapping
    @Operation(summary = "新增证书")
    public Result<Void> add(@RequestBody Certificate cert) {
        certificateService.save(cert);
        return Result.ok();
    }

    /**
     * 修改证书
     */
    @PutMapping("/{id}")
    @Operation(summary = "修改证书")
    public Result<Void> update(@PathVariable Long id, @RequestBody Certificate cert) {
        cert.setId(id);
        certificateService.updateById(cert);
        return Result.ok();
    }

    /**
     * 删除证书
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除证书")
    public Result<Void> delete(@PathVariable Long id) {
        certificateService.removeById(id);
        return Result.ok();
    }

    /**
     * 更新证书状态
     */
    @PatchMapping("/{id}/status")
    @Operation(summary = "更新证书状态")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        certificateService.updateStatus(id, status);
        return Result.ok();
    }

    /**
     * 更新自动续期配置
     */
    @PatchMapping("/{id}/auto-renew")
    @Operation(summary = "更新自动续期配置")
    public Result<Void> updateAutoRenew(
            @PathVariable Long id,
            @RequestParam Integer autoRenew,
            @RequestParam(required = false) Integer renewReminderDays) {
        certificateService.updateAutoRenew(id, autoRenew, renewReminderDays);
        return Result.ok();
    }

    /**
     * 根据路由ID查询关联的证书
     */
    @GetMapping("/route")
    @Operation(summary = "根据路由ID查询关联的证书")
    public Result<List<Certificate>> getCertificatesByRouteId(@RequestParam String routeId) {
        List<Certificate> certs = certificateService.listByRouteId(routeId);
        return Result.ok(certs);
    }

    /**
     * 获取证书已绑定的路由ID列表
     */
    @GetMapping("/bound-routes")
    @Operation(summary = "获取证书已绑定的路由ID列表")
    public Result<List<String>> getBoundRoutes(@RequestParam String certId) {
        List<String> routeIds = certificateService.getBoundRouteIds(certId);
        return Result.ok(routeIds);
    }

    /**
     * 绑定路由到证书
     */
    @PostMapping("/{certId}/bind-routes")
    @Operation(summary = "绑定路由到证书")
    public Result<Void> bindRoutes(@PathVariable String certId, @RequestBody BindRoutesRequest request) {
        certificateService.bindRoutes(certId, request.getRouteIds(), request.getDomain());
        return Result.ok();
    }

    /**
     * 绑定路由请求体
     */
    public static class BindRoutesRequest {
        private List<String> routeIds;
        private String domain;

        public List<String> getRouteIds() {
            return routeIds;
        }

        public void setRouteIds(List<String> routeIds) {
            this.routeIds = routeIds;
        }

        public String getDomain() {
            return domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }
    }
}
