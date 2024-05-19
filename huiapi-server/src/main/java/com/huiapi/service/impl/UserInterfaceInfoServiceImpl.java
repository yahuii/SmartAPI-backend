package com.huiapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huiapi.common.ErrorCode;
import com.huiapi.common.model.entity.User;
import com.huiapi.common.model.entity.UserInterfaceInfo;
import com.huiapi.exception.BusinessException;
import com.huiapi.mapper.UserInterfaceInfoMapper;
import com.huiapi.service.UserInterfaceInfoService;
import com.huiapi.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
* @author 顾琴
* @description 针对表【user_interface_info(接口信息)】的数据库操作Service实现
* @createDate 2024-05-15 19:55:56
*/
@Service
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo>
    implements UserInterfaceInfoService {

    @Resource
    private UserService userService;

    @Override
    public boolean invokeCount(long interfaceInfoId, long userId) {
        if(interfaceInfoId <=0 || userId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("interfaceInfoId",interfaceInfoId).eq("userId",userId);
        UserInterfaceInfo userInterfaceInfo = this.getOne(queryWrapper);
        if(userInterfaceInfo.getLeftNum() == 0){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR,"接口请求次数已用尽");
        }
        UpdateWrapper<UserInterfaceInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("interfaceInfoId",interfaceInfoId);
        updateWrapper.eq("userId",userId);
        updateWrapper.gt("leftNum",0);
        updateWrapper.setSql("leftNum = leftNum - 1,totalNum = totalNum + 1");
        return this.update(updateWrapper);
    }

    @Override
    public UserInterfaceInfo getUserInterfaceInfoByInterfaceId(long interfaceInfoId, HttpServletRequest request) {
        if (interfaceInfoId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();
        LambdaQueryWrapper<UserInterfaceInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserInterfaceInfo::getInterfaceInfoId,interfaceInfoId);
        lambdaQueryWrapper.eq(UserInterfaceInfo::getUserId,userId);

        return this.getOne(lambdaQueryWrapper);
    }
}




