package com.carbon.model.dto.questionSubmit;

import lombok.Data;

@Data
public class JudgeInfo {

    /**
     * 执行时间
     */
    private Long time;

    /**
     * 所用内存
     */
    private Long memory;

    /**
     * 执行信息
     */
    private String message;

}
