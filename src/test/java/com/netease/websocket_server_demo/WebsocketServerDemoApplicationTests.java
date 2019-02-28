package com.netease.websocket_server_demo;

import com.netease.websocket_server_demo.component.HelloClientEndpoint;
//import com.netease.websocket_server_demo.component.HelloEndpoint;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WebSocketServerDemoApplicationTests {

    @Test
    public void contextLoads() {
    }

    @Test
    public void socket() throws URISyntaxException, IOException, DeploymentException {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        Session session = container.connectToServer(HelloClientEndpoint.class, new URI("ws://10.240.171.10:8085/api/v1/chat/1/client"));
        session.getBasicRemote().sendText("client");
    }

    public static void main(String[] args) throws URISyntaxException, IOException, DeploymentException {

    }
}
