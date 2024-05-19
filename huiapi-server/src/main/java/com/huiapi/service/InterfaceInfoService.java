package com.huiapi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.huiapi.common.model.entity.InterfaceInfo;

/**
 *
 * @author 顾琴
 */
public interface InterfaceInfoService extends IService<InterfaceInfo> {

    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);
}
