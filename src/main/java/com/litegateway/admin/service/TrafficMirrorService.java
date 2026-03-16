package com.litegateway.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.litegateway.admin.repository.entity.TrafficMirror;

import java.util.List;

public interface TrafficMirrorService extends IService<TrafficMirror> {

    TrafficMirror getByMirrorId(String mirrorId);

    List<TrafficMirror> getAllEnabled();

    IPage<TrafficMirror> queryPage(Page<TrafficMirror> page, String mirrorName,
                                   String sourceRouteId, String sourceServiceId, Integer enabled);

    List<TrafficMirror> getBySourceRouteId(String routeId);

    List<TrafficMirror> getBySourceServiceId(String serviceId);

    TrafficMirror saveMirror(TrafficMirror mirror);

    boolean updateMirror(TrafficMirror mirror);

    boolean deleteMirror(String mirrorId);

    boolean updateStatus(String mirrorId, Integer enabled);
}
