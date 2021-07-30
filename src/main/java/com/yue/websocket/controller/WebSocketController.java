package com.yue.websocket.controller;

import com.yue.websocket.sever.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * @ProjectName: spring-boot-websocket
 * @Package: com.yue.websocket.controller
 * @ClassName: WebSocketController
 * @Author: YUE
 * @Description:
 * @Date: 2021/7/29 20:28
 * @Version: 1.0
 */
@RestController
@RequestMapping("/webSocket")
public class WebSocketController {

    @Autowired
    private WebSocketServer webSocketServer;

    @GetMapping("/webSocket")
    public String socket() {
        webSocketServer.sendMessage("111111111111111111");
        return null;
    }
}
