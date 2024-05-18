package com.huiapi.huiapiinterface.controller;

import cn.hutool.http.HttpRequest;
import com.huiapiclientsdk.model.Weather;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 顾琴
 */
@RestController
@RequestMapping("/weather")
public class WeatherController {

    private static final String OUTER_INTERFACE_ID = "88888888";

    private static final String OUTER_INTERFACE_KEY = "88888888";


    @PostMapping("/now")
    public String getWeatherByNow(@RequestBody Weather weather){
        if(weather == null){
            throw new RuntimeException();
        }
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("id",OUTER_INTERFACE_ID);
        paramsMap.put("key",OUTER_INTERFACE_KEY);
        paramsMap.put("sheng",weather.getProvince());
        paramsMap.put("place",weather.getPlace());

        return HttpRequest.get("https://cn.apihz.cn/api/tianqi/tqyb.php")
                .form(paramsMap)
                .execute().body();
    }

    @PostMapping("/days")
    public String getWeatherForDays(@RequestBody Weather weather){
        if(weather == null){
            throw new RuntimeException();
        }
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("id",OUTER_INTERFACE_ID);
        paramsMap.put("key",OUTER_INTERFACE_KEY);
        paramsMap.put("sheng",weather.getProvince());
        paramsMap.put("place",weather.getPlace());

        return HttpRequest.get("https://cn.apihz.cn/api/tianqi/tqyb.php")
                .form(paramsMap)
                .execute().body();
    }



}
