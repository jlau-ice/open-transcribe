package com.carbon.result.model.dto;


import lombok.Data;

import java.util.Date;

@Data
public class TranscribeMessage {

    /**
     * 音频id
     */
    private Long audioId;

    /**
     * 转录结果
     */
    private String resultText;

    /**
     * 状态
     */
    private String status;

    /**
     * 任务开始时间
     */
    private Date startTime;

    /**
     * 任务结束时间
     */
    private Date endTime;

}


