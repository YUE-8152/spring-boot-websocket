package com.yue.websocket.job;

import com.yue.websocket.mapper.WebSocketMapper;
import com.yue.websocket.sever.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @ProjectName: spring-boot-websocket
 * @Package: com.yue.websocket.job
 * @ClassName: WebSocketScheduleTask
 * @Author: YUE
 * @Description:
 * @Date: 2021/7/29 21:45
 * @Version: 1.0
 */
@Component
@EnableScheduling
public class WebSocketScheduleTask {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketScheduleTask.class);

    @Autowired
    private WebSocketServer webSocketServer;

    @Autowired
    private WebSocketMapper webSocketMapper;

    @Scheduled(cron = "*/3 * * * * ?")
    public void configureTasks() {
        Integer count = webSocketMapper.selectCount();
        logger.info("===============>总统计数: " + count);
        webSocketServer.sendMessage("+++++++++实时推送的消息++++++"+ count);
        logger.info("===============>执行静态定时任务时间: " + LocalDateTime.now());
    }
}
