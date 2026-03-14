package com.litegateway.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.litegateway.admin.common.web.PageBody;
import com.litegateway.admin.common.web.Result;
import com.litegateway.admin.query.PageQuery;
import com.litegateway.admin.repository.entity.SysUser;
import com.litegateway.admin.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 用户管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/gateway/user")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "系统用户管理相关接口")
public class UserController {

    private final SysUserService sysUserService;

    /**
     * 分页查询用户列表
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询用户", description = "分页查询系统用户列表")
    public Result<PageBody<SysUser>> getUserPage(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "20") Integer pageSize,
            @Parameter(description = "用户名") @RequestParam(required = false) String username,
            @Parameter(description = "真实姓名") @RequestParam(required = false) String realName,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {

        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(SysUser::getCreateTime);

        // 添加查询条件
        if (StringUtils.hasText(username)) {
            wrapper.like(SysUser::getUsername, username);
        }
        if (StringUtils.hasText(realName)) {
            wrapper.like(SysUser::getRealName, realName);
        }
        if (status != null) {
            wrapper.eq(SysUser::getStatus, status);
        }

        Page<SysUser> page = new Page<>(pageNum, pageSize);
        Page<SysUser> resultPage = sysUserService.page(page, wrapper);

        PageBody<SysUser> pageBody = new PageBody<>();
        pageBody.setTotal(resultPage.getTotal());
        pageBody.setPages((int) resultPage.getPages());
        pageBody.setPageSize(Math.toIntExact(resultPage.getSize()));
        pageBody.setPageNum((int) resultPage.getCurrent());
        pageBody.setList(resultPage.getRecords());

        return Result.ok(pageBody);
    }

    /**
     * 获取所有用户列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取用户列表", description = "获取所有系统用户列表")
    public Result<List<SysUser>> getUserList(
            @Parameter(description = "用户名") @RequestParam(required = false) String username,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {

        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(SysUser::getCreateTime);

        if (StringUtils.hasText(username)) {
            wrapper.like(SysUser::getUsername, username);
        }
        if (status != null) {
            wrapper.eq(SysUser::getStatus, status);
        }

        List<SysUser> list = sysUserService.list(wrapper);
        return Result.ok(list);
    }

    /**
     * 根据ID获取用户详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取用户详情", description = "根据ID获取用户详细信息")
    public Result<SysUser> getUserById(@PathVariable Long id) {
        SysUser user = sysUserService.getById(id);
        if (user == null) {
            return Result.failure("A0201", "用户不存在");
        }
        // 清除敏感信息
        user.setPassword(null);
        return Result.ok(user);
    }

    /**
     * 添加用户
     */
    @PostMapping
    @Operation(summary = "添加用户", description = "添加新的系统用户")
    public Result<Void> addUser(@Valid @RequestBody SysUser user) {
        // 检查用户名是否已存在
        SysUser existUser = sysUserService.getByUsername(user.getUsername());
        if (existUser != null) {
            return Result.failure("A0111", "用户名已存在");
        }

        // 密码加密
        if (StringUtils.hasText(user.getPassword())) {
            String encryptedPassword = DigestUtils.md5DigestAsHex(
                    user.getPassword().getBytes(StandardCharsets.UTF_8)
            );
            user.setPassword(encryptedPassword);
        } else {
            // 默认密码 123456
            user.setPassword("e10adc3949ba59abbe56e057f20f883e");
        }

        // 设置默认状态
        if (user.getStatus() == null) {
            user.setStatus(0);
        }

        sysUserService.save(user);
        log.info("User added: {}", user.getUsername());
        return Result.ok();
    }

    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新用户", description = "更新系统用户信息")
    public Result<Void> updateUser(@PathVariable Long id, @Valid @RequestBody SysUser user) {
        SysUser existUser = sysUserService.getById(id);
        if (existUser == null) {
            return Result.failure("A0201", "用户不存在");
        }

        // 检查用户名是否被其他用户使用
        if (StringUtils.hasText(user.getUsername()) && !user.getUsername().equals(existUser.getUsername())) {
            SysUser otherUser = sysUserService.getByUsername(user.getUsername());
            if (otherUser != null && !otherUser.getId().equals(id)) {
                return Result.failure("A0111", "用户名已存在");
            }
        }

        user.setId(id);
        // 不更新密码字段（密码更新使用单独的接口）
        user.setPassword(null);

        sysUserService.updateById(user);
        log.info("User updated: {}", id);
        return Result.ok();
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户", description = "删除系统用户")
    public Result<Void> deleteUser(@PathVariable Long id) {
        SysUser user = sysUserService.getById(id);
        if (user == null) {
            return Result.failure("A0201", "用户不存在");
        }

        // 不允许删除管理员账号
        if ("admin".equals(user.getUsername())) {
            return Result.failure("A0202", "不能删除管理员账号");
        }

        sysUserService.removeById(id);
        log.info("User deleted: {}", id);
        return Result.ok();
    }

    /**
     * 修改用户状态
     */
    @PatchMapping("/{id}/status")
    @Operation(summary = "修改用户状态", description = "启用或禁用用户账号")
    public Result<Void> updateUserStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {

        SysUser user = sysUserService.getById(id);
        if (user == null) {
            return Result.failure("A0201", "用户不存在");
        }

        // 不允许禁用管理员账号
        if ("admin".equals(user.getUsername()) && status == 1) {
            return Result.failure("A0202", "不能禁用管理员账号");
        }

        SysUser updateUser = new SysUser();
        updateUser.setId(id);
        updateUser.setStatus(status);
        sysUserService.updateById(updateUser);

        log.info("User status updated: {} -> {}", id, status);
        return Result.ok();
    }

    /**
     * 重置用户密码
     */
    @PostMapping("/reset-password")
    @Operation(summary = "重置密码", description = "重置用户密码")
    public Result<Void> resetPassword(
            @RequestParam Long id,
            @RequestParam(required = false) String password) {

        SysUser user = sysUserService.getById(id);
        if (user == null) {
            return Result.failure("A0201", "用户不存在");
        }

        // 如果没有提供新密码，使用默认密码 123456
        String newPassword = StringUtils.hasText(password) ? password : "123456";
        String encryptedPassword = DigestUtils.md5DigestAsHex(
                newPassword.getBytes(StandardCharsets.UTF_8)
        );

        SysUser updateUser = new SysUser();
        updateUser.setId(id);
        updateUser.setPassword(encryptedPassword);
        sysUserService.updateById(updateUser);

        log.info("User password reset: {}", id);
        return Result.ok();
    }
}
