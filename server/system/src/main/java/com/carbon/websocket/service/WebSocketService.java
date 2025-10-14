package com.carbon.websocket.service;

import cn.hutool.json.JSONUtil;
import com.carbon.websocket.model.WebSocketResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

@Service
@Slf4j
@ServerEndpoint(value = "/websocket/{userId}")
public class WebSocketService {

    //静态变量，用来记录当前在线连接数。
    private static int onlineCount = 0;
    private Session session;

    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    private static final CopyOnWriteArraySet<WebSocketService> webSockets = new CopyOnWriteArraySet<>();
    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private static final Map<String, Session> sessionPool = new HashMap<>();

    /**
     * 连接建立成功调用的方法
     *
     * @param session session
     * @param userId  userId
     */
    @OnOpen
    public void onOpen(Session session, @PathParam(value = "userId") String userId) {
        this.session = session;
        webSockets.add(this);                       //加入set中
        addOnlineCount();                           //在线数加1
        sessionPool.put(userId, session);           //把对应用户id的session放到sessionPool中，用于单点信息发送
        log.info("【websocket消息】 有新连接加入！用户id:{},当前在线人数为:{}", userId, getOnlineCount());
    }


    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSockets.remove(this);
        subOnlineCount();           //在线数减1
        log.info("【websocket消息】 连接断开！当前在线人数为:{}", getOnlineCount());
    }


    /**
     * 收到客户端消息
     *
     * @param message message
     */
    @OnMessage
    public void onMessage(String message) {
        log.debug("【websocket消息】收到客户端消息:{}", message);
    }

    /**
     * 广播消息
     *
     * @param message message
     */
    public void sendAllMessage(String message) {
        for (WebSocketService webSocket : webSockets) {
            log.debug("【websocket消息】广播消息:{}", message);
            try {
                webSocket.session.getAsyncRemote().sendText(message);
            } catch (Exception e) {
                log.info("【websocket消息】广播消息发送失败:{}", e.getMessage());
            }
        }
    }

    /**
     * 广播消息
     *
     * @param socketResult socketResult
     */
    public <T> void sendAllMessage(WebSocketResult<T> socketResult) {
        sendAllMessage(JSONUtil.toJsonStr(socketResult));
    }

    /**
     * 单点消息
     *
     * @param userId  userId
     * @param message message
     */
    public void sendOneMessage(String userId, String message) {
        try {
            // 防止推送到客户端的信息太多导致弹窗太快
            Thread.sleep(500);
            log.debug("用户{}【websocket消息】单点消息:{}", userId, message);
            Session session = sessionPool.get(userId);
            if (session != null) {
                // getAsyncRemote是异步发送，加锁防止上一个消息还未发完下一个消息又进入了此方法
                // 也就是防止多线程中同一个session多次被调用报错,虽然上面睡了0.5秒，为了保险最好加锁
                synchronized (session) {
                    session.getAsyncRemote().sendText(message);
                }
            }
        } catch (Exception e) {
            log.info("【websocket消息】单点消息发送失败:{}", e.getMessage());
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.info("socket发生错误:{}", error.getMessage());
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketService.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketService.onlineCount--;
    }

}


