package com.example.websocket2.config;

import com.sun.org.apache.xerces.internal.impl.xpath.XPath;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 前后端交互的类实现消息的接收推送(自己发送给所有人(不包括自己))
 *
 * @ServerEndpoint(value = "/test/oneToMany") 前端通过此URI 和后端交互，建立连接
 */
@Slf4j
@ServerEndpoint(value = "/test/oneToMany")
@Component
public class OneToManyWebSocket {

    private static AtomicInteger onlineCount = new AtomicInteger(0);

    //存放在线的客户端
    private static Map<String, Session> clients = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        onlineCount.incrementAndGet();
        clients.put(session.getId(), session);
        log.info("有新连接加入: {}, 当前在线人数: {}", session.getId(), onlineCount.get());
    }
    @OnClose
    public void onClose(Session session) {
        onlineCount.decrementAndGet();
        clients.remove(session.getId());
        log.info("有一连接断开: {}, 当前在线人数: {}", session.getId(), onlineCount.get());
    }
    @OnMessage
    public void onMessage(Session session, String message) {
        log.info("服务端收到客户端: {}, 的消息: {}", session.getId(), message);
        this.sendMessage(message, session);
    }
    @OnError
    public void onError(Session session, Throwable error) {
        log.info("发生错误");
        error.printStackTrace();
    }

    /**
     * 群发消息功能
     * @param message
     * @param FromSession
     */
    public void sendMessage(String message, Session FromSession) {
        for(Map.Entry<String, Session> sessionFactory : clients.entrySet()) {
            Session toSession = sessionFactory.getValue();
            //排除自己(发信息的)
            if (!toSession.equals(FromSession)) {
                log.info("服务端给客户端[{}]发送消息{}", toSession.getId(), message);
                toSession.getAsyncRemote().sendText(message);
            }
        }
    }

}
