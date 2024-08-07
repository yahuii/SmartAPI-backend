package com.huiapigateway;

import cn.hutool.json.JSONUtil;
import com.alibaba.nacos.shaded.com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.huiapi.common.model.entity.InterfaceInfo;
import com.huiapi.common.model.entity.User;
import com.huiapi.common.service.InnerInterfaceInfoService;
import com.huiapi.common.service.InnerUserInterfaceInfoService;
import com.huiapi.common.service.InnerUserService;
import com.huiapiclientsdk.util.SignUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author 顾琴
 */
@Slf4j
@Component
public class CustomGlobalFilter implements GlobalFilter, Ordered {


//    private List<String> IP_WHITE_LIST = Arrays.asList("127.0.0.1");


    @DubboReference
    private InnerInterfaceInfoService innerInterfaceInfoService;

    @DubboReference
    private InnerUserService innerUserService;

    @Resource
    private Gson gson;

    @DubboReference
    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;


    private static final String INTERFACE_HOST = "http://localhost:8080";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        //请求日志
        ServerHttpRequest request = exchange.getRequest();
        log.info("请求唯一标识" + request.getId());
        String path = INTERFACE_HOST + request.getPath().value();
        log.info("请求路径" + path);
        String method = request.getMethodValue();
        log.info("请求类型" + method);
        //访问控制 - 黑白名单
        String hostString = request.getLocalAddress().getHostString();
        ServerHttpResponse response = exchange.getResponse();
//        if(!IP_WHITE_LIST.contains(hostString)){
//            return handleNoAuth(response);
//        }

        //用户鉴权 AK SK 合法判断
        HttpHeaders headers = request.getHeaders();
        String accessKey = headers.getFirst("accessKey");
        String body = headers.getFirst("body");
        String nonce = headers.getFirst("nonce");
        String timestamp = headers.getFirst("timestamp");
        //客户传的密钥签名
        String sign = headers.getFirst("sign");

        User invokeUser = innerUserService.getInvokeUser(accessKey);
        if(invokeUser == null){
            return handleNoAuth(response);
        }

        // 解决中文乱码问题
        try {
            body = new String(body.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Error decoding body", e);
            return handleNoAuth(response);
        }

        String serverAccessKey = invokeUser.getAccessKey();
        String serverSecretKey = invokeUser.getSecretKey();
        if(! serverAccessKey.equals(accessKey)){
            return handleNoAuth(response);
        }
        if(Long.parseLong(nonce) > 10000){
            return handleNoAuth(response);
        }
        //时间与当前时间不得超过5分钟
        long FIVE_MINUTES = 5 * 60L;
        if(Math.abs(System.currentTimeMillis() / 1000 - Long.parseLong(timestamp)) >= FIVE_MINUTES){
            return handleNoAuth(response);
        }

        //客户应有的密钥签名（使用数据库中的密钥）
        String serverSign = SignUtils.genSecret(body, serverSecretKey);

        if(!serverSign.equals(sign)){
            return handleNoAuth(response);
        }

        // 从数据库中查询模拟接口是否存在，以及请求方法是否匹配
        InterfaceInfo interfaceInfo = innerInterfaceInfoService.getInterfaceInfo(path, method);
        if(interfaceInfo == null){
            return handleNoAuth(response);
        }

        //响应日志
        return handleResponse(exchange,chain,interfaceInfo.getId(),invokeUser.getId());
    }

    private Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain,
                                      Long interfaceInfoId,Long userId) {
        try {
            // 从交换机拿到原始response
            ServerHttpResponse originalResponse = exchange.getResponse();
            // 缓冲区工厂 拿到缓存数据
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            // 拿到状态码
            HttpStatus statusCode = originalResponse.getStatusCode();

            if (statusCode == HttpStatus.OK) {
                // 装饰，增强能力
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                    // 等调用完转发的接口后才会执行
                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        log.info("body instanceof Flux: {}", (body instanceof Flux));
                        // 对象是响应式的
                        if (body instanceof Flux) {
                            // 我们拿到真正的body
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                            // 往返回值里面写数据
                            // 拼接字符串
                            return super.writeWith(fluxBody.map(dataBuffer -> {
                                // 调用成功，接口调用次数+1
                                try {
                                    boolean b = innerUserInterfaceInfoService.invokeCount(interfaceInfoId,userId);
                                } catch (Exception e) {
                                    log.error("invoke error",e);
                                }
                                // data从这个content中读取
                                byte[] content = new byte[dataBuffer.readableByteCount()];
                                dataBuffer.read(content);
                                DataBufferUtils.release(dataBuffer);// 释放掉内存
                                // 6.构建日志
                                List<Object> rspArgs = new ArrayList<>();
                                rspArgs.add(originalResponse.getStatusCode());
                                String data = new String(content, StandardCharsets.UTF_8);// data
                                rspArgs.add(data);
                                log.info("<--- status:{} data:{}"// data
                                        , rspArgs.toArray());// log.info("<-- {} {}", originalResponse.getStatusCode(), data);
                                return bufferFactory.wrap(content);
                            }));
                        } else {
                            // 8.调用失败返回错误状态码
                            log.error("<--- {} 响应code异常", getStatusCode());
                        }
                        return super.writeWith(body);
                    }
                };
                // 设置 response 对象为装饰过的
                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            }
            return chain.filter(exchange);// 降级处理返回数据
        } catch (Exception e) {
            log.error("gateway log exception.\n" + e);
            return chain.filter(exchange);
        }

    }



    @Override
    public int getOrder() {
        return -2;
    }

    public Mono<Void> handleNoAuth(ServerHttpResponse response){
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }

}
