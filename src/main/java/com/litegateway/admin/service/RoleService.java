package com.litegateway.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.litegateway.admin.repository.entity.Role;

import java.util.List;

/**
 * 角色服务接口
 */
public interface RoleService extends IService<Role> {

    /**
     * 根据用户ID查询角色列表
     */
    List<Role> getRolesByUserId(Long userId);

    /**
     * 分配用户角色
     */
    void assignUserRoles(Long userId, List<Long> roleIds);

    /**
     * 分配角色权限
     */
    void assignRolePermissions(Long roleId, List<Long> permissionIds);
}
