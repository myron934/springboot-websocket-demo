package com.netease.websocket_server_demo.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;

/**
 * @author weiminglun
 */
@ClientEndpoint
@Component
public class HelloClientEndpoint {
    private static final Logger logger = LoggerFactory.getLogger(HelloClientEndpoint.class);

    @OnOpen
    public void onOpen(Session session) {
        logger.info("客户端建立连接成功");
    }

    @OnMessage
    public void onMessage(String message) {
        logger.info("Client onMessage: " + message);
    }

    @OnClose
    public void onClose() {
        logger.info("客户端关闭");
    }
}
