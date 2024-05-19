package com.huiapi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huiapi.common.model.entity.UserInterfaceInfo;

import java.util.List;

/**
* @author 顾琴
* @description 针对表【user_interface_info(接口信息)】的数据库操作Mapper
* @createDate 2024-05-15 19:55:56
* @Entity com.huiapi.model.entity.UserInterfaceInfo
*/
public interface UserInterfaceInfoMapper extends BaseMapper<UserInterfaceInfo> {

    List<UserInterfaceInfo> listTopInvokeInterfaceInfo(int limit);
}




