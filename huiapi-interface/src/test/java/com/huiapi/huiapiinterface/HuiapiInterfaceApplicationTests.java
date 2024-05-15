package com.huiapi.huiapiinterface;

import cn.hutool.core.lang.Assert;
import com.huiapiclientsdk.client.HuiApiClient;
import com.huiapiclientsdk.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class HuiapiInterfaceApplicationTests {

    @Resource
    private HuiApiClient huiApiClient;

    @Test
    void contextLoads() {
        String yahui = huiApiClient.getUserNameByPost(new User("yahui"));
        System.out.println(yahui);
    }

}
