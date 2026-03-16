package com.litegateway.admin.web.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.litegateway.admin.common.web.Result;
import com.litegateway.admin.common.exception.ErrorCode;
import com.litegateway.admin.repository.entity.TrafficMirror;
import com.litegateway.admin.service.TrafficMirrorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/gateway/traffic-mirror")
public class TrafficMirrorController {

    @Autowired
    private TrafficMirrorService trafficMirrorService;

    @GetMapping("/list")
    public Result<IPage<TrafficMirror>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String mirrorName,
            @RequestParam(required = false) String sourceRouteId,
            @RequestParam(required = false) String sourceServiceId,
            @RequestParam(required = false) Integer enabled) {
        Page<TrafficMirror> pageParam = new Page<>(page, size);
        IPage<TrafficMirror> result = trafficMirrorService.queryPage(pageParam, mirrorName, sourceRouteId, sourceServiceId, enabled);
        return Result.success(result);
    }

    @GetMapping("/{mirrorId}")
    public Result<TrafficMirror> getById(@PathVariable String mirrorId) {
        TrafficMirror mirror = trafficMirrorService.getByMirrorId(mirrorId);
        if (mirror == null) {
            return Result.fail(ErrorCode.RESOURCE_NOT_FOUND);
        }
        return Result.success(mirror);
    }

    @PostMapping
    public Result<TrafficMirror> create(@RequestBody TrafficMirror mirror) {
        TrafficMirror saved = trafficMirrorService.saveMirror(mirror);
        return Result.success(saved);
    }

    @PutMapping("/{mirrorId}")
    public Result<TrafficMirror> update(@PathVariable String mirrorId, @RequestBody TrafficMirror mirror) {
        mirror.setMirrorId(mirrorId);
        boolean success = trafficMirrorService.updateMirror(mirror);
        if (success) {
            return Result.success(mirror);
        }
        return Result.fail(ErrorCode.SYSTEM_ERROR);
    }

    @DeleteMapping("/{mirrorId}")
    public Result<Void> delete(@PathVariable String mirrorId) {
        boolean success = trafficMirrorService.deleteMirror(mirrorId);
        if (success) {
            return Result.success();
        }
        return Result.fail(ErrorCode.SYSTEM_ERROR);
    }

    @PutMapping("/{mirrorId}/status")
    public Result<Void> updateStatus(@PathVariable String mirrorId, @RequestParam Integer enabled) {
        boolean success = trafficMirrorService.updateStatus(mirrorId, enabled);
        if (success) {
            return Result.success();
        }
        return Result.fail(ErrorCode.SYSTEM_ERROR);
    }

    @GetMapping("/enabled/all")
    public Result<List<TrafficMirror>> getAllEnabled() {
        List<TrafficMirror> list = trafficMirrorService.getAllEnabled();
        return Result.success(list);
    }

    @GetMapping("/sample-modes")
    public Result<List<String>> getSampleModes() {
        return Result.success(Arrays.asList("PERCENTAGE", "COUNT", "ALL"));
    }

    @GetMapping("/protocols")
    public Result<List<String>> getProtocols() {
        return Result.success(Arrays.asList("HTTP", "HTTPS", "GRPC"));
    }
}
