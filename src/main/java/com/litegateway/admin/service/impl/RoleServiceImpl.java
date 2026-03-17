package com.litegateway.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.litegateway.admin.repository.entity.Role;
import com.litegateway.admin.repository.entity.RolePermission;
import com.litegateway.admin.repository.entity.UserRole;
import com.litegateway.admin.repository.mapper.RoleMapper;
import com.litegateway.admin.repository.mapper.RolePermissionMapper;
import com.litegateway.admin.repository.mapper.UserRoleMapper;
import com.litegateway.admin.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色服务实现
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    private final UserRoleMapper userRoleMapper;
    private final RolePermissionMapper rolePermissionMapper;

    @Override
    public List<Role> getRolesByUserId(Long userId) {
        return baseMapper.selectRolesByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignUserRoles(Long userId, List<Long> roleIds) {
        // 删除原有角色关联
        userRoleMapper.delete(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UserRole>()
                .eq(UserRole::getUserId, userId));

        // 添加新的角色关联
        for (Long roleId : roleIds) {
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRole.setCreatedAt(LocalDateTime.now());
            userRoleMapper.insert(userRole);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRolePermissions(Long roleId, List<Long> permissionIds) {
        // 删除原有权限关联
        rolePermissionMapper.delete(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<RolePermission>()
                .eq(RolePermission::getRoleId, roleId));

        // 添加新的权限关联
        for (Long permissionId : permissionIds) {
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRoleId(roleId);
            rolePermission.setPermissionId(permissionId);
            rolePermission.setCreatedAt(LocalDateTime.now());
            rolePermissionMapper.insert(rolePermission);
        }
    }
}
