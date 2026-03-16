package com.litegateway.admin.web.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.litegateway.admin.common.web.Result;
import com.litegateway.admin.common.exception.ErrorCode;
import com.litegateway.admin.repository.entity.ProtocolConversion;
import com.litegateway.admin.service.ProtocolConversionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/gateway/protocol-conversion")
public class ProtocolConversionController {

    @Autowired
    private ProtocolConversionService protocolConversionService;

    @GetMapping("/list")
    public Result<IPage<ProtocolConversion>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String sourceProtocol,
            @RequestParam(required = false) String targetProtocol,
            @RequestParam(required = false) Integer enabled) {
        Page<ProtocolConversion> pageParam = new Page<>(page, size);
        IPage<ProtocolConversion> result = protocolConversionService.queryPage(pageParam, name, sourceProtocol, targetProtocol, enabled);
        return Result.success(result);
    }

    @GetMapping("/{conversionId}")
    public Result<ProtocolConversion> getById(@PathVariable String conversionId) {
        ProtocolConversion conversion = protocolConversionService.getByConversionId(conversionId);
        if (conversion == null) {
            return Result.fail(ErrorCode.RESOURCE_NOT_FOUND);
        }
        return Result.success(conversion);
    }

    @PostMapping
    public Result<ProtocolConversion> create(@RequestBody ProtocolConversion conversion) {
        ProtocolConversion saved = protocolConversionService.saveConversion(conversion);
        return Result.success(saved);
    }

    @PutMapping("/{conversionId}")
    public Result<ProtocolConversion> update(@PathVariable String conversionId, @RequestBody ProtocolConversion conversion) {
        conversion.setConversionId(conversionId);
        boolean success = protocolConversionService.updateConversion(conversion);
        if (success) {
            return Result.success(conversion);
        }
        return Result.fail(ErrorCode.SYSTEM_ERROR);
    }

    @DeleteMapping("/{conversionId}")
    public Result<Void> delete(@PathVariable String conversionId) {
        boolean success = protocolConversionService.deleteConversion(conversionId);
        if (success) {
            return Result.success();
        }
        return Result.fail(ErrorCode.SYSTEM_ERROR);
    }

    @PutMapping("/{conversionId}/status")
    public Result<Void> updateStatus(@PathVariable String conversionId, @RequestParam Integer enabled) {
        boolean success = protocolConversionService.updateStatus(conversionId, enabled);
        if (success) {
            return Result.success();
        }
        return Result.fail(ErrorCode.SYSTEM_ERROR);
    }

    @GetMapping("/enabled/all")
    public Result<List<ProtocolConversion>> getAllEnabled() {
        List<ProtocolConversion> list = protocolConversionService.getAllEnabled();
        return Result.success(list);
    }

    @GetMapping("/protocols")
    public Result<List<String>> getSupportedProtocols() {
        return Result.success(Arrays.asList("HTTP", "HTTPS", "gRPC", "Dubbo", "WebSocket"));
    }
}
