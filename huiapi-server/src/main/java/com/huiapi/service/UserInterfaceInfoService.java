package com.huiapi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.huiapi.common.model.entity.UserInterfaceInfo;

import javax.servlet.http.HttpServletRequest;

/**
* @author 顾琴
* @description 针对表【user_interface_info(接口信息)】的数据库操作Service
* @createDate 2024-05-15 19:55:56
*/
public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {


    /**
     * 调用接口统计
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    boolean invokeCount(long interfaceInfoId,long userId);

    UserInterfaceInfo getUserInterfaceInfoByInterfaceId(long interfaceInfoId, HttpServletRequest request);


}
