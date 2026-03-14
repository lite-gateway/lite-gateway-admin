package com.litegateway.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.litegateway.admin.common.web.PageBody;
import com.litegateway.admin.common.web.Result;
import com.litegateway.admin.constants.RedisTypeConstants;
import com.litegateway.admin.repository.entity.IpBlacklist;
import com.litegateway.admin.repository.mapper.IpBlacklistMapper;
import com.litegateway.admin.service.ConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * IP黑名单管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/gateway/ip-blacklist")
@RequiredArgsConstructor
@Tag(name = "IP黑名单管理", description = "IP黑名单管理相关接口")
public class IpBlacklistController {

    private final IpBlacklistMapper ipBlacklistMapper;
    private final StringRedisTemplate redisTemplate;
    private final ConfigService configService;

    /**
     * 分页查询IP黑名单列表
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询IP黑名单", description = "分页查询IP黑名单列表")
    public Result<PageBody<IpBlacklist>> getIpBlacklistPage(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "20") Integer pageSize,
            @Parameter(description = "IP地址") @RequestParam(required = false) String ip) {

        LambdaQueryWrapper<IpBlacklist> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(IpBlacklist::getCreateTime);

        if (StringUtils.hasText(ip)) {
            wrapper.like(IpBlacklist::getIp, ip);
        }

        Page<IpBlacklist> page = new Page<>(pageNum, pageSize);
        Page<IpBlacklist> resultPage = ipBlacklistMapper.selectPage(page, wrapper);

        PageBody<IpBlacklist> pageBody = new PageBody<>();
        pageBody.setTotal(resultPage.getTotal());
        pageBody.setPages((int) resultPage.getPages());
        pageBody.setPageSize((int) resultPage.getSize());
        pageBody.setPageNum((int) resultPage.getCurrent());
        pageBody.setList(resultPage.getRecords());

        return Result.ok(pageBody);
    }

    /**
     * 获取所有IP黑名单列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取IP黑名单列表", description = "获取所有IP黑名单列表")
    public Result<List<IpBlacklist>> getIpBlacklist(
            @Parameter(description = "IP地址") @RequestParam(required = false) String ip) {

        LambdaQueryWrapper<IpBlacklist> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(IpBlacklist::getCreateTime);

        if (StringUtils.hasText(ip)) {
            wrapper.like(IpBlacklist::getIp, ip);
        }

        List<IpBlacklist> list = ipBlacklistMapper.selectList(wrapper);
        return Result.ok(list);
    }

    /**
     * 根据ID获取IP黑名单详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取IP黑名单详情", description = "根据ID获取IP黑名单详细信息")
    public Result<IpBlacklist> getIpBlacklistById(@PathVariable Long id) {
        IpBlacklist ipBlacklist = ipBlacklistMapper.selectById(id);
        if (ipBlacklist == null) {
            return Result.failure("B0001", "IP黑名单记录不存在");
        }
        return Result.ok(ipBlacklist);
    }

    /**
     * 添加IP黑名单
     */
    @PostMapping
    @Operation(summary = "添加IP黑名单", description = "添加新的IP黑名单")
    public Result<Void> addIpBlacklist(@Valid @RequestBody IpBlacklist ipBlacklist) {
        // 检查IP是否已存在
        LambdaQueryWrapper<IpBlacklist> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(IpBlacklist::getIp, ipBlacklist.getIp());
        Long count = ipBlacklistMapper.selectCount(wrapper);
        if (count > 0) {
            return Result.failure("B0002", "该IP已存在");
        }

        ipBlacklistMapper.insert(ipBlacklist);
        log.info("IpBlacklist added: {}", ipBlacklist.getIp());
        
        // 发布配置变更通知
        publishIpUpdate();
        
        return Result.ok();
    }

    /**
     * 更新IP黑名单
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新IP黑名单", description = "更新IP黑名单信息")
    public Result<Void> updateIpBlacklist(@PathVariable Long id, @Valid @RequestBody IpBlacklist ipBlacklist) {
        IpBlacklist exist = ipBlacklistMapper.selectById(id);
        if (exist == null) {
            return Result.failure("B0001", "IP黑名单记录不存在");
        }

        // 检查IP是否被其他记录使用
        if (StringUtils.hasText(ipBlacklist.getIp()) && !ipBlacklist.getIp().equals(exist.getIp())) {
            LambdaQueryWrapper<IpBlacklist> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(IpBlacklist::getIp, ipBlacklist.getIp());
            Long count = ipBlacklistMapper.selectCount(wrapper);
            if (count > 0) {
                return Result.failure("B0002", "该IP已存在");
            }
        }

        ipBlacklist.setId(id);
        ipBlacklistMapper.updateById(ipBlacklist);
        log.info("IpBlacklist updated: {}", id);
        
        // 发布配置变更通知
        publishIpUpdate();
        
        return Result.ok();
    }

    /**
     * 删除IP黑名单
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除IP黑名单", description = "删除IP黑名单")
    public Result<Void> deleteIpBlacklist(@PathVariable Long id) {
        IpBlacklist exist = ipBlacklistMapper.selectById(id);
        if (exist == null) {
            return Result.failure("B0001", "IP黑名单记录不存在");
        }

        ipBlacklistMapper.deleteById(id);
        log.info("IpBlacklist deleted: {}", id);
        
        // 发布配置变更通知
        publishIpUpdate();
        
        return Result.ok();
    }

    /**
     * 批量删除IP黑名单
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除IP黑名单", description = "批量删除IP黑名单")
    public Result<Void> batchDeleteIpBlacklist(@RequestParam List<Long> ids) {
        ipBlacklistMapper.deleteBatchIds(ids);
        log.info("IpBlacklist batch deleted: {}", ids);
        
        // 发布配置变更通知
        publishIpUpdate();
        
        return Result.ok();
    }

    /**
     * 检查IP是否在黑名单中
     */
    @GetMapping("/check")
    @Operation(summary = "检查IP", description = "检查IP是否在黑名单中")
    public Result<Boolean> checkIpInBlacklist(@RequestParam String ip) {
        LambdaQueryWrapper<IpBlacklist> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(IpBlacklist::getIp, ip);
        Long count = ipBlacklistMapper.selectCount(wrapper);
        return Result.ok(count > 0);
    }
    
    /**
     * 发布IP黑名单更新消息到Redis
     */
    private void publishIpUpdate() {
        try {
            // 增加配置版本号
            configService.incrementVersion();
            // 发布Redis消息通知所有Gateway实例
            redisTemplate.convertAndSend(RedisTypeConstants.CHANNEL, RedisTypeConstants.IP_UPDATE);
            log.info("Published IP blacklist update message to Redis channel: {}", RedisTypeConstants.CHANNEL);
        } catch (Exception e) {
            log.error("Failed to publish IP blacklist update message to Redis", e);
        }
    }
}
