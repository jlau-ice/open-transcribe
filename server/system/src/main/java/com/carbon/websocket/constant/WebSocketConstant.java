package com.carbon.websocket.constant;

public class WebSocketConstant {
    public static final String HEARTBEAT_TYPE = "heartbeat";
    public static final String MESSAGE_TYPE = "message";
    public static final long HEARTBEAT_INTERVAL = 60000; // 60秒
    // WebSocket 状态码
    public static final int NORMAL_CLOSURE = 1000;
    public static final int GOING_AWAY = 1001;
}


