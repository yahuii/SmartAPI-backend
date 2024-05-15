package com.huiapiclientsdk;

import com.huiapiclientsdk.client.HuiApiClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author 顾琴
 */

@Configuration
@ConfigurationProperties("huiapi.client")
@Data
@ComponentScan
public class HuiApiClientConfig {

    private String accessKey;

    private String secretKey;

    @Bean
    public HuiApiClient getHuiApiClient(){
        return new HuiApiClient(accessKey,secretKey);
    }


}
