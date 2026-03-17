package com.litegateway.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.litegateway.admin.common.web.Result;
import com.litegateway.admin.repository.entity.Permission;
import com.litegateway.admin.repository.mapper.PermissionMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 权限管理Controller
 */
@Tag(name = "权限管理", description = "权限的增删改查")
@RestController
@RequestMapping("/gateway/permission")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionMapper permissionMapper;

    /**
     * 查询权限树
     */
    @Operation(summary = "查询权限树")
    @GetMapping("/tree")
    public Result<List<Permission>> tree(
            @Parameter(description = "权限类型") @RequestParam(required = false) String permissionType) {

        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<Permission>()
                .eq(Permission::getStatus, 1)
                .orderByAsc(Permission::getSortOrder);

        if (permissionType != null && !permissionType.isEmpty()) {
            wrapper.eq(Permission::getPermissionType, permissionType);
        }

        List<Permission> list = permissionMapper.selectList(wrapper);
        return Result.success(list);
    }

    /**
     * 获取权限详情
     */
    @Operation(summary = "获取权限详情")
    @GetMapping("/{id}")
    public Result<Permission> getById(@PathVariable Long id) {
        Permission permission = permissionMapper.selectById(id);
        if (permission == null) {
            return Result.error("权限不存在");
        }
        return Result.success(permission);
    }

    /**
     * 创建权限
     */
    @Operation(summary = "创建权限")
    @PostMapping
    public Result<Long> create(@RequestBody @Validated Permission permission) {
        permission.setCreatedAt(LocalDateTime.now());
        permission.setUpdatedAt(LocalDateTime.now());

        permissionMapper.insert(permission);
        return Result.success(permission.getId());
    }

    /**
     * 更新权限
     */
    @Operation(summary = "更新权限")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody @Validated Permission permission) {
        Permission existing = permissionMapper.selectById(id);
        if (existing == null) {
            return Result.error("权限不存在");
        }

        permission.setId(id);
        permission.setUpdatedAt(LocalDateTime.now());

        permissionMapper.updateById(permission);
        return Result.success();
    }

    /**
     * 删除权限
     */
    @Operation(summary = "删除权限")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        permissionMapper.deleteById(id);
        return Result.success();
    }

    /**
     * 获取角色的权限列表
     */
    @Operation(summary = "获取角色的权限列表")
    @GetMapping("/role/{roleId}")
    public Result<List<Permission>> getPermissionsByRoleId(@PathVariable Long roleId) {
        List<Permission> list = permissionMapper.selectPermissionsByRoleId(roleId);
        return Result.success(list);
    }

    /**
     * 获取用户的权限列表
     */
    @Operation(summary = "获取用户的权限列表")
    @GetMapping("/user/{userId}")
    public Result<List<Permission>> getPermissionsByUserId(@PathVariable Long userId) {
        List<Permission> list = permissionMapper.selectPermissionsByUserId(userId);
        return Result.success(list);
    }
}
