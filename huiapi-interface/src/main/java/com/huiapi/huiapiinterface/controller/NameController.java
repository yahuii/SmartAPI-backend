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

    @PostMapping("/post")
    public String getNameByPost(@RequestParam String name){
        return "POST 名称为" + name;
    }

    @PostMapping("/user")
    public String getUserNameByPost(@RequestBody User user){

        String result = "POST 用户名称为" + user.getName();

        return result;
    }



}
