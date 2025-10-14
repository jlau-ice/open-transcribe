package com.carbon.websocket.model;


import lombok.Data;

@Data
public class SuccessNotification {

    private String status;

    private Long audioId;

    private String message;

}


