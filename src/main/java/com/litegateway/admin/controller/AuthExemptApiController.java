package com.litegateway.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.litegateway.admin.common.web.Result;
import com.litegateway.admin.repository.entity.AuthExemptApi;
import com.litegateway.admin.repository.mapper.AuthExemptApiMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 免鉴权API管理Controller
 */
@Tag(name = "免鉴权API管理", description = "免鉴权API的增删改查")
@RestController
@RequestMapping("/gateway/auth-exempt")
@RequiredArgsConstructor
public class AuthExemptApiController {

    private final AuthExemptApiMapper exemptApiMapper;

    /**
     * 分页查询免鉴权API列表
     */
    @Operation(summary = "分页查询免鉴权API列表")
    @GetMapping
    public Result<Page<AuthExemptApi>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "API路径") @RequestParam(required = false) String apiPath,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        
        LambdaQueryWrapper<AuthExemptApi> wrapper = new LambdaQueryWrapper<AuthExemptApi>()
                .orderByDesc(AuthExemptApi::getCreatedAt);
        
        if (apiPath != null && !apiPath.isEmpty()) {
            wrapper.like(AuthExemptApi::getApiPath, apiPath);
        }
        
        if (status != null) {
            wrapper.eq(AuthExemptApi::getStatus, status);
        }
        
        Page<AuthExemptApi> result = exemptApiMapper.selectPage(new Page<>(page, size), wrapper);
        return Result.success(result);
    }

    /**
     * 获取所有生效的免鉴权API
     */
    @Operation(summary = "获取所有生效的免鉴权API")
    @GetMapping("/active")
    public Result<List<AuthExemptApi>> listActive() {
        List<AuthExemptApi> list = exemptApiMapper.selectActiveExemptApis();
        return Result.success(list);
    }

    /**
     * 获取免鉴权API详情
     */
    @Operation(summary = "获取免鉴权API详情")
    @GetMapping("/{id}")
    public Result<AuthExemptApi> getById(@PathVariable Long id) {
        AuthExemptApi entity = exemptApiMapper.selectById(id);
        if (entity == null) {
            return Result.error("记录不存在");
        }
        return Result.success(entity);
    }

    /**
     * 创建免鉴权API
     */
    @Operation(summary = "创建免鉴权API")
    @PostMapping
    public Result<Long> create(@RequestBody @Validated AuthExemptApi entity) {
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        
        exemptApiMapper.insert(entity);
        return Result.success(entity.getId());
    }

    /**
     * 更新免鉴权API
     */
    @Operation(summary = "更新免鉴权API")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody @Validated AuthExemptApi entity) {
        AuthExemptApi existing = exemptApiMapper.selectById(id);
        if (existing == null) {
            return Result.error("记录不存在");
        }
        
        entity.setId(id);
        entity.setUpdatedAt(LocalDateTime.now());
        
        exemptApiMapper.updateById(entity);
        return Result.success();
    }

    /**
     * 删除免鉴权API
     */
    @Operation(summary = "删除免鉴权API")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        exemptApiMapper.deleteById(id);
        return Result.success();
    }

    /**
     * 启用/禁用免鉴权API
     */
    @Operation(summary = "启用/禁用免鉴权API")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        AuthExemptApi entity = new AuthExemptApi();
        entity.setId(id);
        entity.setStatus(status);
        entity.setUpdatedAt(LocalDateTime.now());
        
        exemptApiMapper.updateById(entity);
        return Result.success();
    }
}
