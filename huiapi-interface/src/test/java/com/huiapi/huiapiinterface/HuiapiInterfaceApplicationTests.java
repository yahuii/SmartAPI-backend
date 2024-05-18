package com.huiapi.huiapiinterface;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import com.huiapiclientsdk.client.HuiApiClient;
import com.huiapiclientsdk.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SpringBootTest
class HuiapiInterfaceApplicationTests {

    @Resource
    private HuiApiClient huiApiClient;

    @Test
    void contextLoads() {
        String yahui = huiApiClient.getUserNameByPost(new User("yahui"));
        System.out.println(yahui);
    }

    @Test
    void testAPI(){
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("id","88888888");
        paramsMap.put("key","88888888");
        paramsMap.put("sheng","湖南");
        paramsMap.put("place","衡阳");


        String body = HttpRequest.get("https://cn.apihz.cn/api/tianqi/tqybmoji15.php")
                .form(paramsMap)
                .execute().body();

        HashMap bean = JSONUtil.toBean(body, HashMap.class);



//        System.out.println(list.get(0));

//        System.out.println(o);
//        System.out.println(bean);

//        System.out.println(body);


    }

}
