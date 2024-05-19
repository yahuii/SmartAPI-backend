package com.huiapi.common.service;

import com.huiapi.common.model.entity.User;

/**
 * @author 顾琴
 */
public interface InnerUserService {
    /**
     * 获取调用的用户信息
     * @return
     */
    User getInvokeUser(String accessKey);
}
