package com.yue.websocket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @ProjectName: spring-boot-websocket
 * @Package: com.yue.websocket.config
 * @ClassName: WebSocketConfig
 * @Author: YUE
 * @Description:
 * @Date: 2021/7/29 20:13
 * @Version: 1.0
 */
@Configuration
public class WebSocketConfig {
    /**
     * 用途：扫描并注册所有携带@ServerEndpoint注解的实例。
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
