package com.huiapi.huiapiinterface.controller;


import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.huiapiclientsdk.model.AIContent;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/AI")
public class AIController {

    @PostMapping("/chat")
    public String chatWithAI(@RequestBody AIContent content){

        String body = HttpRequest.post("https://api.deepseek.com/chat/completions")
                .body("{\n  \"messages\": [\n    {\n      \"content\": \"You are a helpful assistant\",\n      \"role\": \"system\"\n    },\n    {\n      \"content\": \"" + content + "\",\n      \"role\": \"user\"\n    }\n  ],\n  \"model\": \"deepseek-chat\",\n  \"frequency_penalty\": 0,\n  \"max_tokens\": 2048,\n  \"presence_penalty\": 0,\n  \"stop\": null,\n  \"stream\": false,\n  \"temperature\": 1,\n  \"top_p\": 1,\n  \"logprobs\": false,\n  \"top_logprobs\": null\n}")
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", "Bearer sk-829e11a04bf84d6bb5d5dd72076658db")
                .execute().body();
        JSONObject jsonObject = new JSONObject(body);
        JSONArray jsonArray = (JSONArray) jsonObject.get("choices");

        return ((JSONObject)((JSONObject)jsonArray.get(0)).get("message")).get("content").toString();
    }

}
