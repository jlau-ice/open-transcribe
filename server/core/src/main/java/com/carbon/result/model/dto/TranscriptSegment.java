package com.carbon.result.model.dto;


import lombok.Data;

@Data
public class TranscriptSegment {

    /**
     * 段落id
     */
    private String id;

    /**
     * 开始时间戳
     */
    private Double start;

    /**
     * 结束时间戳
     */
    private Double end;

    /**
     * 内容
     */
    private String text;

    /**
     * 说话的人
     */
    private String speaker;
}
