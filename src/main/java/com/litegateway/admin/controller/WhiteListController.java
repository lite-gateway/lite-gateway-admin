package com.litegateway.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.litegateway.admin.common.web.PageBody;
import com.litegateway.admin.common.web.Result;
import com.litegateway.admin.constants.RedisTypeConstants;
import com.litegateway.admin.repository.entity.WhiteList;
import com.litegateway.admin.repository.mapper.WhiteListMapper;
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
 * 白名单管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/gateway/whitelist")
@RequiredArgsConstructor
@Tag(name = "白名单管理", description = "白名单管理相关接口")
public class WhiteListController {

    private final WhiteListMapper whiteListMapper;
    private final StringRedisTemplate redisTemplate;
    private final ConfigService configService;

    /**
     * 分页查询白名单列表
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询白名单", description = "分页查询白名单列表")
    public Result<PageBody<WhiteList>> getWhiteListPage(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "20") Integer pageSize,
            @Parameter(description = "路径") @RequestParam(required = false) String path) {

        LambdaQueryWrapper<WhiteList> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(WhiteList::getCreateTime);

        if (StringUtils.hasText(path)) {
            wrapper.like(WhiteList::getPath, path);
        }

        Page<WhiteList> page = new Page<>(pageNum, pageSize);
        Page<WhiteList> resultPage = whiteListMapper.selectPage(page, wrapper);

        PageBody<WhiteList> pageBody = new PageBody<>();
        pageBody.setTotal(resultPage.getTotal());
        pageBody.setPages((int) resultPage.getPages());
        pageBody.setPageSize((int) resultPage.getSize());
        pageBody.setPageNum((int) resultPage.getCurrent());
        pageBody.setList(resultPage.getRecords());

        return Result.ok(pageBody);
    }

    /**
     * 获取所有白名单列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取白名单列表", description = "获取所有白名单列表")
    public Result<List<WhiteList>> getWhiteList(
            @Parameter(description = "路径") @RequestParam(required = false) String path) {

        LambdaQueryWrapper<WhiteList> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(WhiteList::getCreateTime);

        if (StringUtils.hasText(path)) {
            wrapper.like(WhiteList::getPath, path);
        }

        List<WhiteList> list = whiteListMapper.selectList(wrapper);
        return Result.ok(list);
    }

    /**
     * 根据ID获取白名单详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取白名单详情", description = "根据ID获取白名单详细信息")
    public Result<WhiteList> getWhiteListById(@PathVariable Long id) {
        WhiteList whiteList = whiteListMapper.selectById(id);
        if (whiteList == null) {
            return Result.failure("W0001", "白名单记录不存在");
        }
        return Result.ok(whiteList);
    }

    /**
     * 添加白名单
     */
    @PostMapping
    @Operation(summary = "添加白名单", description = "添加新的白名单")
    public Result<Void> addWhiteList(@Valid @RequestBody WhiteList whiteList) {
        // 检查路径是否已存在
        LambdaQueryWrapper<WhiteList> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WhiteList::getPath, whiteList.getPath());
        Long count = whiteListMapper.selectCount(wrapper);
        if (count > 0) {
            return Result.failure("W0002", "该路径已存在");
        }

        whiteListMapper.insert(whiteList);
        log.info("WhiteList added: {}", whiteList.getPath());
        
        // 发布配置变更通知
        publishWhiteListUpdate();
        
        return Result.ok();
    }

    /**
     * 更新白名单
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新白名单", description = "更新白名单信息")
    public Result<Void> updateWhiteList(@PathVariable Long id, @Valid @RequestBody WhiteList whiteList) {
        WhiteList exist = whiteListMapper.selectById(id);
        if (exist == null) {
            return Result.failure("W0001", "白名单记录不存在");
        }

        // 检查路径是否被其他记录使用
        if (StringUtils.hasText(whiteList.getPath()) && !whiteList.getPath().equals(exist.getPath())) {
            LambdaQueryWrapper<WhiteList> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(WhiteList::getPath, whiteList.getPath());
            Long count = whiteListMapper.selectCount(wrapper);
            if (count > 0) {
                return Result.failure("W0002", "该路径已存在");
            }
        }

        whiteList.setId(id);
        whiteListMapper.updateById(whiteList);
        log.info("WhiteList updated: {}", id);
        
        // 发布配置变更通知
        publishWhiteListUpdate();
        
        return Result.ok();
    }

    /**
     * 删除白名单
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除白名单", description = "删除白名单")
    public Result<Void> deleteWhiteList(@PathVariable Long id) {
        WhiteList exist = whiteListMapper.selectById(id);
        if (exist == null) {
            return Result.failure("W0001", "白名单记录不存在");
        }

        whiteListMapper.deleteById(id);
        log.info("WhiteList deleted: {}", id);
        
        // 发布配置变更通知
        publishWhiteListUpdate();
        
        return Result.ok();
    }

    /**
     * 批量删除白名单
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除白名单", description = "批量删除白名单")
    public Result<Void> batchDeleteWhiteList(@RequestParam List<Long> ids) {
        whiteListMapper.deleteBatchIds(ids);
        log.info("WhiteList batch deleted: {}", ids);
        
        // 发布配置变更通知
        publishWhiteListUpdate();
        
        return Result.ok();
    }
    
    /**
     * 发布白名单更新消息到Redis
     */
    private void publishWhiteListUpdate() {
        try {
            // 增加配置版本号
            configService.incrementVersion();
            // 发布Redis消息通知所有Gateway实例
            redisTemplate.convertAndSend(RedisTypeConstants.CHANNEL, RedisTypeConstants.WHITE_LIST_UPDATE);
            log.info("Published whitelist update message to Redis channel: {}", RedisTypeConstants.CHANNEL);
        } catch (Exception e) {
            log.error("Failed to publish whitelist update message to Redis", e);
        }
    }
}
