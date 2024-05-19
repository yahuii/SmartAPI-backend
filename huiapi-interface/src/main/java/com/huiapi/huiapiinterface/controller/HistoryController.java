package com.huiapi.huiapiinterface.controller;

import cn.hutool.http.HttpRequest;
import com.huiapiclientsdk.model.User;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static com.huiapi.huiapiinterface.constant.FreeAPIConstant.OUTER_INTERFACE_ID;
import static com.huiapi.huiapiinterface.constant.FreeAPIConstant.OUTER_INTERFACE_KEY;

/**
 * @author 顾琴
 */
@RestController
@RequestMapping("/history")
public class HistoryController {

    @GetMapping("/today")
    public String getTodayHistory(){
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("id",OUTER_INTERFACE_ID);
        paramsMap.put("key",OUTER_INTERFACE_KEY);

        return HttpRequest.get("https://cn.apihz.cn/api/zici/today.php")
                .form(paramsMap)
                .execute().body();
    }



}
