package com.netease.websocket_server_demo.controller;

import com.netease.websocket_server_demo.component.HelloEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/develop", produces = "application/json;charset=UTF-8")
public class DevelopController {

    private static final Logger logger = LoggerFactory.getLogger(DevelopController.class);

    @RequestMapping(value = "login", method = {RequestMethod.GET, RequestMethod.POST})
    public String login(@RequestParam(value = "mail") String mail) {
        return "登陆成功:" + mail;
    }

    @PostMapping(value = "message")
    public String sendMessage(@RequestParam(value = "chatId") String chatId, @RequestParam(value = "message") String message) {
        HelloEndpoint.sendMessageToAll(chatId, message);
        return "发送成功了";
    }

}
