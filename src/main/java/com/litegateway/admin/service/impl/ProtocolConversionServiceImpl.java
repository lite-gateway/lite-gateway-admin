package com.litegateway.admin.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.litegateway.admin.repository.entity.ProtocolConversion;
import com.litegateway.admin.repository.mapper.ProtocolConversionMapper;
import com.litegateway.admin.service.ProtocolConversionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ProtocolConversionServiceImpl extends ServiceImpl<ProtocolConversionMapper, ProtocolConversion>
        implements ProtocolConversionService {

    @Override
    public ProtocolConversion getByConversionId(String conversionId) {
        return baseMapper.selectByConversionId(conversionId);
    }

    @Override
    public List<ProtocolConversion> getAllEnabled() {
        return baseMapper.selectAllEnabled();
    }

    @Override
    public IPage<ProtocolConversion> queryPage(Page<ProtocolConversion> page, String name,
                                               String sourceProtocol, String targetProtocol, Integer enabled) {
        return baseMapper.selectByPage(page, name, sourceProtocol, targetProtocol, enabled);
    }

    @Override
    public List<ProtocolConversion> getByRouteId(String routeId) {
        return baseMapper.selectByRouteId(routeId);
    }

    @Override
    public List<ProtocolConversion> getByServiceId(String serviceId) {
        return baseMapper.selectByServiceId(serviceId);
    }

    @Override
    public ProtocolConversion saveConversion(ProtocolConversion conversion) {
        if (StringUtils.isBlank(conversion.getConversionId())) {
            conversion.setConversionId("pc_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        }
        conversion.setCreateTime(LocalDateTime.now());
        conversion.setUpdateTime(LocalDateTime.now());
        if (conversion.getEnabled() == null) {
            conversion.setEnabled(1);
        }
        save(conversion);
        return conversion;
    }

    @Override
    public boolean updateConversion(ProtocolConversion conversion) {
        ProtocolConversion existing = getByConversionId(conversion.getConversionId());
        if (existing == null) {
            return false;
        }
        conversion.setId(existing.getId());
        conversion.setUpdateTime(LocalDateTime.now());
        return updateById(conversion);
    }

    @Override
    public boolean deleteConversion(String conversionId) {
        ProtocolConversion existing = getByConversionId(conversionId);
        if (existing == null) {
            return false;
        }
        return removeById(existing.getId());
    }

    @Override
    public boolean updateStatus(String conversionId, Integer enabled) {
        ProtocolConversion existing = getByConversionId(conversionId);
        if (existing == null) {
            return false;
        }
        existing.setEnabled(enabled);
        existing.setUpdateTime(LocalDateTime.now());
        return updateById(existing);
    }
}
