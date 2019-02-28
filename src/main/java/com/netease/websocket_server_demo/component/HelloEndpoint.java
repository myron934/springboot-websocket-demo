package com.netease.websocket_server_demo.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author weiminglun
 */
@ServerEndpoint(value = "/api/v1/chat/{chatId}/{token}")
@Component
public class HelloEndpoint {
    private static final Logger logger = LoggerFactory.getLogger(HelloEndpoint.class);
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;
    //chatId => userId=>Session>
    private static Map<String, ConcurrentHashMap<String, HelloEndpoint>> chatMap = new HashMap<>();
    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;
    private String userId;
    private String chatId;
    private static final Object lock = new Object();

    @OnOpen
    public void onOpen(Session session, @PathParam("chatId") String chatId, @PathParam("token") String token) {
        logger.info("token={}", token);
        //TODO 验证token是否有效

        this.userId = token;
        this.session = session;
        this.chatId = chatId;
        ConcurrentHashMap<String, HelloEndpoint> endPointMap = chatMap.get(chatId);
        if (endPointMap == null) {
            synchronized (lock) {
                endPointMap = chatMap.get(chatId);
                if (endPointMap == null) {
                    endPointMap = new ConcurrentHashMap<>();
                    chatMap.put(chatId, endPointMap);
                }
            }
        }
        HelloEndpoint oldEndpoint = endPointMap.get(userId);
        if (oldEndpoint != null) {
            try {
                oldEndpoint.session.close();
                logger.info("old session for{} is closed", userId);
            } catch (IOException e) {
                logger.info("close old session failed", e);
            }
        }
        endPointMap.put(userId, this);     //加入set中
        addOnlineCount();           //在线数加1
        logger.warn("有新连接加入！当前在线人数为" + getOnlineCount());
        sendMessageToAll(chatId, "[系统消息]: " + "欢迎" + userId + "加入聊天");
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        ConcurrentHashMap<String, HelloEndpoint> endPointMap = getEndpointMap(chatId);
        if (endPointMap == null) {
            return;
        }
        endPointMap.remove(userId);
        if (endPointMap.isEmpty()) {
            synchronized (lock) {
                if (endPointMap.isEmpty()) {
                    chatMap.remove(chatId);
                }
            }
        }
        subOnlineCount();           //在线数减1
        logger.warn("有一连接关闭！当前在线人数为" + getOnlineCount());
        sendMessageToAll(chatId, "[系统消息]: " + userId + "退出了群聊");
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message) {
        logger.info("来自客户端 {" + userId + "} 的消息: " + message);
        if (StringUtils.isEmpty(message)) {
            return;
        }
        ConcurrentHashMap<String, HelloEndpoint> endPointMap = getEndpointMap(chatId);
        if (endPointMap == null) {
            return;
        }
        //群发消息
        endPointMap.forEach((k, v) -> {
            try {
                synchronized (v.session) {
                    if (userId.equals(v.userId)) {
                        v.session.getBasicRemote().sendText("<b style=\"color:#D00\">[" + "我" + "]</b>: " + message);
                    } else {
                        v.session.getBasicRemote().sendText("<b style=\"color:#0DD\">[" + userId + "]</b>: " + message);
                    }

                }
            } catch (IOException e) {
                logger.info("send message to{} failed", k, e);
            }
        });

    }

    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Throwable error) {
        logger.info("something wrong with session {}", session.getId(), error);
    }

    public void sendMessage(String message) throws IOException {
        session.getBasicRemote().sendText(message);
    }

    /**
     * 群发自定义消息
     */
    public static void sendMessageToAll(String chatId, String message) {
        ConcurrentHashMap<String, HelloEndpoint> endPointMap = getEndpointMap(chatId);
        if (endPointMap == null) {
            return;
        }
        endPointMap.forEach((k, v) -> {
            try {
                if (v.session.isOpen()) {
                    synchronized (v.session) {
                        if (v.session.isOpen()) {
                            v.session.getBasicRemote().sendText(message);
                        }
                    }
                }
            } catch (IOException e) {
                logger.info("send message to{} failed", k, e);
            }
        });
    }

    private static ConcurrentHashMap<String, HelloEndpoint> getEndpointMap(String chatId) {
        synchronized (lock) {
            return chatMap.get(chatId);
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        HelloEndpoint.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        HelloEndpoint.onlineCount--;
    }
}
