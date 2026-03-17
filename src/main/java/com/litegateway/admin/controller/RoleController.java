package com.litegateway.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.litegateway.admin.repository.entity.Role;
import com.litegateway.admin.service.RoleService;
import com.litegateway.admin.common.web.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色权限管理Controller
 */
@Tag(name = "角色权限管理", description = "角色和权限的增删改查")
@RestController
@RequestMapping("/gateway/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    /**
     * 分页查询角色列表
     */
    @Operation(summary = "分页查询角色列表")
    @GetMapping
    public Result<Page<Role>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword) {
        
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<Role>()
                .orderByDesc(Role::getCreatedAt);
        
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Role::getRoleName, keyword)
                    .or()
                    .like(Role::getRoleCode, keyword));
        }
        
        Page<Role> result = roleService.page(new Page<>(page, size), wrapper);
        return Result.success(result);
    }

    /**
     * 获取所有启用的角色
     */
    @Operation(summary = "获取所有启用的角色")
    @GetMapping("/enabled")
    public Result<List<Role>> listEnabled() {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<Role>()
                .eq(Role::getStatus, 1)
                .orderByAsc(Role::getSortOrder);
        
        List<Role> list = roleService.list(wrapper);
        return Result.success(list);
    }

    /**
     * 获取角色详情
     */
    @Operation(summary = "获取角色详情")
    @GetMapping("/{id}")
    public Result<Role> getById(@PathVariable Long id) {
        Role role = roleService.getById(id);
        if (role == null) {
            return Result.error("角色不存在");
        }
        return Result.success(role);
    }

    /**
     * 创建角色
     */
    @Operation(summary = "创建角色")
    @PostMapping
    public Result<Long> create(@RequestBody @Validated Role role) {
        role.setCreatedAt(LocalDateTime.now());
        role.setUpdatedAt(LocalDateTime.now());
        
        roleService.save(role);
        return Result.success(role.getId());
    }

    /**
     * 更新角色
     */
    @Operation(summary = "更新角色")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody @Validated Role role) {
        Role existing = roleService.getById(id);
        if (existing == null) {
            return Result.error("角色不存在");
        }
        
        role.setId(id);
        role.setUpdatedAt(LocalDateTime.now());
        
        roleService.updateById(role);
        return Result.success();
    }

    /**
     * 删除角色
     */
    @Operation(summary = "删除角色")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        roleService.removeById(id);
        return Result.success();
    }

    /**
     * 启用/禁用角色
     */
    @Operation(summary = "启用/禁用角色")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        Role role = new Role();
        role.setId(id);
        role.setStatus(status);
        role.setUpdatedAt(LocalDateTime.now());
        
        roleService.updateById(role);
        return Result.success();
    }

    /**
     * 分配用户角色
     */
    @Operation(summary = "分配用户角色")
    @PostMapping("/assign-user-roles")
    public Result<Void> assignUserRoles(@RequestParam Long userId, @RequestBody List<Long> roleIds) {
        roleService.assignUserRoles(userId, roleIds);
        return Result.success();
    }

    /**
     * 分配角色权限
     */
    @Operation(summary = "分配角色权限")
    @PostMapping("/assign-role-permissions")
    public Result<Void> assignRolePermissions(@RequestParam Long roleId, @RequestBody List<Long> permissionIds) {
        roleService.assignRolePermissions(roleId, permissionIds);
        return Result.success();
    }
}
