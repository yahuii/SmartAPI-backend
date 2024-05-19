package com.huiapi.huiapiinterface.controller;

import cn.hutool.http.HttpRequest;
import com.huiapiclientsdk.model.TranslateContent;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 顾琴
 */

@RestController
@RequestMapping("/translate")
public class TranslateController {


    @GetMapping("/get")
    public String translateContent(@RequestBody TranslateContent translateContent){
        Map<String,Object> paramsMap = new HashMap<>();
        paramsMap.put("msg",translateContent.getContent());
        paramsMap.put("type","3");
        return HttpRequest.get("https://v.api.aa1.cn/api/api-fanyi-yd/index.php")
                .form(paramsMap)
                .execute().body();

    }

}
