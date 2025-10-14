package com.carbon.websocket.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProgressMessage {
    private String type;
    private Long totalTime;
    private int total;
    private int current;
    private double percentage;
    private String status;

    public ProgressMessage(int current, int total) {
        this.current = current;
        this.total = total;
        this.percentage = (double) current / total * 100;
        this.status = String.format("处理中: %d/%d (%.2f%%)", current, total, percentage);
    }

    public ProgressMessage(int current, int total, String type) {
        this.type = type;
        this.current = current;
        this.total = total;
        this.percentage = (double) current / total * 100;
        this.status = String.format("处理中: %d/%d (%.2f%%)", current, total, percentage);
    }

    public ProgressMessage(int total, Long totalTime) {
        this.total  = total;
        this.totalTime = totalTime;
        this.status = "success";
    }
}


