package com.huiapi;


import cn.hutool.http.HttpRequest;
import com.huiapiclientsdk.client.HuiApiClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 主类测试
 *
 */
@SpringBootTest
class MainApplicationTests {

    @Test
    public void testReflect(){

        HuiApiClient tempClient = new HuiApiClient("huiapi","abcdefg");

        String body = HttpRequest.get("http://localhost:8080/api/history/today")
                .execute().body();
        System.out.println(body);

//        String result = tempClient.getTodayHistory();
//        Method method = null;
//        Object result = null;
//        try {
//            method = HuiApiClient.class.getMethod("getWeatherByNow", Weather.class);
//            result =  method.invoke(tempClient,new Weather("湖南","衡阳"));
////            method = HuiApiClient.class.getMethod("getUserNameByPost", User.class);
////            result = method.invoke(tempClient,new User("guqin"));
//        } catch (Exception e) {
//            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
//        }
//        System.out.println(result);
    }

}
