package com.huiapi.huiapiinterface.controller;

import com.huiapiclientsdk.model.User;
import com.huiapiclientsdk.util.SignUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 顾琴
 */
@RestController
@RequestMapping("/name")
public class NameController {

    @GetMapping("/")
    public String getNameByGet(@RequestParam String name){

        return "GET 名称为" + name;
    }

    @PostMapping("/")
    public String getNameByPost(@RequestParam String name){
        return "POST 名称为" + name;
    }

    @PostMapping("/user")
    public String getUserNameByPost(@RequestBody User user, HttpServletRequest request){
        String accessKey = request.getHeader("accessKey");
        String body = request.getHeader("body");
        String nonce = request.getHeader("nonce");
        String timestamp = request.getHeader("timestamp");
        String sign = request.getHeader("sign");
        // todo 实际情况应前往数据库查询此AK、SK是否已分配给用户
        if(!"huiapi".equals(accessKey)){
            throw new RuntimeException("accessKey错误");
        }
        if(Long.parseLong(nonce) > 10000){
            throw new RuntimeException("随机数有误");
        }
        //时间与当前时间不得超过5分钟
        if(Math.abs(System.currentTimeMillis() / 1000 - Long.parseLong(timestamp)) >= 5 * 60){
            throw new RuntimeException("时间已过期");
        }

        // todo 实际情况是从数据库中查出secretKey
        String serverSign = SignUtils.genSecret(body, "abcdefg");

        if(!serverSign.equals(sign)){
            throw new RuntimeException("签名错误");
        }
        return "POST 用户名称为" + user.getName();
    }



}
