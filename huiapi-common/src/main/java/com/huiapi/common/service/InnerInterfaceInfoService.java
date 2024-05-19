package com.huiapi.common.service;

import com.huiapi.common.model.entity.InterfaceInfo;

/**
 * @author 顾琴
 */
public interface InnerInterfaceInfoService {
    /**
     * 从数据库中查询模拟接口是否存在（请求路径、请求方法、请求参数）
     * @return
     */
    InterfaceInfo getInterfaceInfo(String url, String method);
}
