package com.litegateway.admin.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.litegateway.admin.repository.entity.TrafficMirror;
import com.litegateway.admin.repository.mapper.TrafficMirrorMapper;
import com.litegateway.admin.service.TrafficMirrorService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TrafficMirrorServiceImpl extends ServiceImpl<TrafficMirrorMapper, TrafficMirror>
        implements TrafficMirrorService {

    @Override
    public TrafficMirror getByMirrorId(String mirrorId) {
        return baseMapper.selectByMirrorId(mirrorId);
    }

    @Override
    public List<TrafficMirror> getAllEnabled() {
        return baseMapper.selectAllEnabled();
    }

    @Override
    public IPage<TrafficMirror> queryPage(Page<TrafficMirror> page, String mirrorName,
                                          String sourceRouteId, String sourceServiceId, Integer enabled) {
        return baseMapper.selectByPage(page, mirrorName, sourceRouteId, sourceServiceId, enabled);
    }

    @Override
    public List<TrafficMirror> getBySourceRouteId(String routeId) {
        return baseMapper.selectBySourceRouteId(routeId);
    }

    @Override
    public List<TrafficMirror> getBySourceServiceId(String serviceId) {
        return baseMapper.selectBySourceServiceId(serviceId);
    }

    @Override
    public TrafficMirror saveMirror(TrafficMirror mirror) {
        if (StringUtils.isBlank(mirror.getMirrorId())) {
            mirror.setMirrorId("mirror_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        }
        mirror.setCreateTime(LocalDateTime.now());
        mirror.setUpdateTime(LocalDateTime.now());
        if (mirror.getEnabled() == null) {
            mirror.setEnabled(1);
        }
        if (mirror.getMirrorPercentage() == null) {
            mirror.setMirrorPercentage(100);
        }
        if (StringUtils.isBlank(mirror.getSampleMode())) {
            mirror.setSampleMode("PERCENTAGE");
        }
        if (mirror.getCopyRequestBody() == null) {
            mirror.setCopyRequestBody(1);
        }
        if (mirror.getCopyResponseBody() == null) {
            mirror.setCopyResponseBody(0);
        }
        if (mirror.getAsyncMode() == null) {
            mirror.setAsyncMode(1);
        }
        if (mirror.getTimeout() == null) {
            mirror.setTimeout(30000L);
        }
        save(mirror);
        return mirror;
    }

    @Override
    public boolean updateMirror(TrafficMirror mirror) {
        TrafficMirror existing = getByMirrorId(mirror.getMirrorId());
        if (existing == null) {
            return false;
        }
        mirror.setId(existing.getId());
        mirror.setUpdateTime(LocalDateTime.now());
        return updateById(mirror);
    }

    @Override
    public boolean deleteMirror(String mirrorId) {
        TrafficMirror existing = getByMirrorId(mirrorId);
        if (existing == null) {
            return false;
        }
        return removeById(existing.getId());
    }

    @Override
    public boolean updateStatus(String mirrorId, Integer enabled) {
        TrafficMirror existing = getByMirrorId(mirrorId);
        if (existing == null) {
            return false;
        }
        existing.setEnabled(enabled);
        existing.setUpdateTime(LocalDateTime.now());
        return updateById(existing);
    }
}
