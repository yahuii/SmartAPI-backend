package com.huiapi.common.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.huiapi.common.model.entity.User;

/**
 * 用户服务
 *
 * @author 顾琴
 */
public interface InnerUserService{

    /**
     * 获取调用的用户信息
     * @return
     */
    User getInvokeUser(String accessKey);
}
