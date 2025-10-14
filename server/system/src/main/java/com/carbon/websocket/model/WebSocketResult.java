package com.carbon.websocket.model;

import lombok.Data;

/**
 * 统一处理socket返回数据
 **/
@Data
public class WebSocketResult<T>{
    private String type;
    private T data;

    public static <T> WebSocketResult<T> success(String type,  T data){
        WebSocketResult<T> socketResult = new WebSocketResult<>();
        socketResult.setType(type);
        socketResult.setData(data);
        return socketResult;
    }
}
