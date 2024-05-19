package com.huiapi;


import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.huiapiclientsdk.client.HuiApiClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

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

    @Test
    public void testAI(){
        String content = "你好";
        String body = HttpRequest.post("https://api.deepseek.com/chat/completions")
                .body("{\n  \"messages\": [\n    {\n      \"content\": \"You are a helpful assistant\",\n      \"role\": \"system\"\n    },\n    {\n      \"content\": \"" + content + "\",\n      \"role\": \"user\"\n    }\n  ],\n  \"model\": \"deepseek-chat\",\n  \"frequency_penalty\": 0,\n  \"max_tokens\": 2048,\n  \"presence_penalty\": 0,\n  \"stop\": null,\n  \"stream\": false,\n  \"temperature\": 1,\n  \"top_p\": 1,\n  \"logprobs\": false,\n  \"top_logprobs\": null\n}")
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", "Bearer sk-829e11a04bf84d6bb5d5dd72076658db")
                .execute().body();
        JSONObject jsonObject = new JSONObject(body);
        JSONArray jsonArray = (JSONArray) jsonObject.get("choices");
         String result = ((JSONObject)((JSONObject)jsonArray.get(0)).get("message")).get("content").toString();

        System.out.println(result);
    }

    @Test
    public void testTranslate(){
        Map<String,Object> paramsMap = new HashMap<>();
        paramsMap.put("msg","我爱你");
        paramsMap.put("type","3");
        String body = HttpRequest.get("https://v.api.aa1.cn/api/api-fanyi-yd/index.php")
                .form(paramsMap)
                .execute().body();
        System.out.println(body);
    }





}
