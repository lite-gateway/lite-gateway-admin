package com.litegateway.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.litegateway.admin.repository.entity.ProtocolConversion;

import java.util.List;

public interface ProtocolConversionService extends IService<ProtocolConversion> {

    ProtocolConversion getByConversionId(String conversionId);

    List<ProtocolConversion> getAllEnabled();

    IPage<ProtocolConversion> queryPage(Page<ProtocolConversion> page, String name,
                                        String sourceProtocol, String targetProtocol, Integer enabled);

    List<ProtocolConversion> getByRouteId(String routeId);

    List<ProtocolConversion> getByServiceId(String serviceId);

    ProtocolConversion saveConversion(ProtocolConversion conversion);

    boolean updateConversion(ProtocolConversion conversion);

    boolean deleteConversion(String conversionId);

    boolean updateStatus(String conversionId, Integer enabled);
}
