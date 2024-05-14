package com.huiapi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.huiapi.model.entity.InterfaceInfo;

/**
 *
 */
public interface InterfaceInfoService extends IService<InterfaceInfo> {

    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);
}
