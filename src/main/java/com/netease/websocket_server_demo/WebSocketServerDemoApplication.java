package com.netease.websocket_server_demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.netease.websocket_server_demo"})
public class WebSocketServerDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebSocketServerDemoApplication.class, args);
    }

}
