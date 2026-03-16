package com.litegateway.admin.web.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.litegateway.admin.common.web.Result;
import com.litegateway.admin.common.exception.ErrorCode;
import com.litegateway.admin.repository.entity.Plugin;
import com.litegateway.admin.service.PluginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/gateway/plugin")
public class PluginController {

    @Autowired
    private PluginService pluginService;

    @GetMapping("/list")
    public Result<IPage<Plugin>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String pluginName,
            @RequestParam(required = false) String pluginType,
            @RequestParam(required = false) String executePhase,
            @RequestParam(required = false) Integer enabled) {
        Page<Plugin> pageParam = new Page<>(page, size);
        IPage<Plugin> result = pluginService.queryPage(pageParam, pluginName, pluginType, executePhase, enabled);
        return Result.success(result);
    }

    @GetMapping("/{pluginId}")
    public Result<Plugin> getById(@PathVariable String pluginId) {
        Plugin plugin = pluginService.getByPluginId(pluginId);
        if (plugin == null) {
            return Result.fail(ErrorCode.RESOURCE_NOT_FOUND);
        }
        return Result.success(plugin);
    }

    @PostMapping
    public Result<Plugin> create(@RequestBody Plugin plugin) {
        Plugin saved = pluginService.savePlugin(plugin);
        return Result.success(saved);
    }

    @PutMapping("/{pluginId}")
    public Result<Plugin> update(@PathVariable String pluginId, @RequestBody Plugin plugin) {
        plugin.setPluginId(pluginId);
        boolean success = pluginService.updatePlugin(plugin);
        if (success) {
            return Result.success(plugin);
        }
        return Result.fail(ErrorCode.SYSTEM_ERROR);
    }

    @DeleteMapping("/{pluginId}")
    public Result<Void> delete(@PathVariable String pluginId) {
        boolean success = pluginService.deletePlugin(pluginId);
        if (success) {
            return Result.success();
        }
        return Result.fail(ErrorCode.SYSTEM_ERROR);
    }

    @PutMapping("/{pluginId}/status")
    public Result<Void> updateStatus(@PathVariable String pluginId, @RequestParam Integer enabled) {
        boolean success = pluginService.updateStatus(pluginId, enabled);
        if (success) {
            return Result.success();
        }
        return Result.fail(ErrorCode.SYSTEM_ERROR);
    }

    @GetMapping("/enabled/all")
    public Result<List<Plugin>> getAllEnabled() {
        List<Plugin> list = pluginService.getAllEnabled();
        return Result.success(list);
    }

    @GetMapping("/plugin-types")
    public Result<List<String>> getPluginTypes() {
        return Result.success(Arrays.asList("AUTH", "TRANSFORM", "LOG", "RATE_LIMIT", "CUSTOM"));
    }

    @GetMapping("/execute-phases")
    public Result<List<String>> getExecutePhases() {
        return Result.success(Arrays.asList("REQUEST", "RESPONSE", "BOTH"));
    }
}
