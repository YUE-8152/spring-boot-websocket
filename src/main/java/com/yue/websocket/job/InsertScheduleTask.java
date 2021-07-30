package com.yue.websocket.job;

import com.yue.websocket.mapper.WebSocketMapper;
import com.yue.websocket.sever.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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
public class InsertScheduleTask {

    private static final Logger logger = LoggerFactory.getLogger(InsertScheduleTask.class);

    @Autowired
    private WebSocketServer webSocketServer;

    @Autowired
    private WebSocketMapper webSocketMapper;

    @Scheduled(cron = "*/10 * * * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void configureTasks() {
        Map<String,Object> map = new HashMap<>();
        map.put("merCode","100000");
        map.put("storeId",8);
        map.put("childMerCode","10000001");
        map.put("mealTime",new Random(10)+"");
        map.put("createTime","2021-07-30 10:12:00");
        map.put("createUser","Admin");
        map.put("updateTime","2021-07-30 10:12:00");
        map.put("updateUser","Admin");
        webSocketMapper.insert(map);
        logger.info("===============>插入数据成功: " + LocalDateTime.now());
    }
}
