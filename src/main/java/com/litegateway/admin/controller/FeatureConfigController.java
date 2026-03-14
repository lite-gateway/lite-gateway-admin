package com.litegateway.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.litegateway.admin.common.web.PageBody;
import com.litegateway.admin.common.web.Result;
import com.litegateway.admin.repository.entity.GatewayFeatureConfig;
import com.litegateway.admin.service.GatewayFeatureConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 功能配置管理控制器
 */
@RestController
@RequestMapping("/gateway/feature-config")
@RequiredArgsConstructor
public class FeatureConfigController {

    @Autowired
    private GatewayFeatureConfigService featureConfigService;

    /**
     * 分页查询功能配置
     */
    @GetMapping("/page")
    public Result<PageBody<GatewayFeatureConfig>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String featureName,
            @RequestParam(required = false) Boolean enabled) {

        Page<GatewayFeatureConfig> page = featureConfigService.queryPage(featureName, enabled, pageNum, pageSize);

        PageBody<GatewayFeatureConfig> pageBody = new PageBody<>();
        pageBody.setList(page.getRecords());
        pageBody.setTotal(page.getTotal());
        pageBody.setPages((int) page.getPages());
        pageBody.setPageSize((int) page.getSize());
        pageBody.setPageNum((int) page.getCurrent());

        return Result.ok(pageBody);
    }

    /**
     * 查询所有启用的功能配置
     */
    @GetMapping("/list")
    public Result<List<GatewayFeatureConfig>> listAll() {
        List<GatewayFeatureConfig> list = featureConfigService.listAllEnabled();
        return Result.ok(list);
    }

    /**
     * 根据ID查询功能配置
     */
    @GetMapping("/{id}")
    public Result<GatewayFeatureConfig> getById(@PathVariable Long id) {
        GatewayFeatureConfig config = featureConfigService.getById(id);
        return Result.ok(config);
    }

    /**
     * 根据功能编码查询
     */
    @GetMapping("/code/{featureCode}")
    public Result<GatewayFeatureConfig> getByFeatureCode(@PathVariable String featureCode) {
        GatewayFeatureConfig config = featureConfigService.getByFeatureCode(featureCode);
        return Result.ok(config);
    }

    /**
     * 新增功能配置
     */
    @PostMapping
    public Result<Void> add(@RequestBody GatewayFeatureConfig config) {
        featureConfigService.save(config);
        return Result.ok();
    }

    /**
     * 修改功能配置
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody GatewayFeatureConfig config) {
        config.setId(id);
        featureConfigService.updateById(config);
        return Result.ok();
    }

    /**
     * 删除功能配置
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        featureConfigService.removeById(id);
        return Result.ok();
    }

    /**
     * 修改功能配置状态
     */
    @PatchMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Boolean enabled) {
        featureConfigService.updateStatus(id, enabled);
        return Result.ok();
    }

    /**
     * 批量修改功能配置状态
     */
    @PatchMapping("/batch-status")
    public Result<Void> batchUpdateStatus(@RequestBody BatchStatusRequest request) {
        featureConfigService.batchUpdateStatus(request.getIds(), request.getEnabled());
        return Result.ok();
    }

    /**
     * 批量状态修改请求
     */
    public static class BatchStatusRequest {
        private List<Long> ids;
        private Boolean enabled;

        public List<Long> getIds() {
            return ids;
        }

        public void setIds(List<Long> ids) {
            this.ids = ids;
        }

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }
    }
}
