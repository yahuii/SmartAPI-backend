package com.huiapi;


import javax.annotation.Resource;

import com.huiapi.common.ErrorCode;
import com.huiapi.exception.BusinessException;
import com.huiapiclientsdk.client.HuiApiClient;
import com.huiapiclientsdk.model.User;
import com.huiapiclientsdk.model.Weather;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Method;

/**
 * 主类测试
 *
 */
@SpringBootTest
class MainApplicationTests {

    @Test
    public void testReflect(){

        HuiApiClient tempClient = new HuiApiClient("huiapi","abcdefg");

        Method method = null;
        Object result = null;
        try {
            method = HuiApiClient.class.getMethod("getWeatherByNow", Weather.class);
            result =  method.invoke(tempClient,new Weather("湖南","衡阳"));
//            method = HuiApiClient.class.getMethod("getUserNameByPost", User.class);
//            result = method.invoke(tempClient,new User("guqin"));
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        System.out.println(result);
    }

}
