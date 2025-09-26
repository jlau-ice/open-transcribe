package com.carbon.result.model.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class TranscribeResult {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

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

    /**
     * 保留字段1
     */
    private String temp1;

    /**
     * 保留字段2
     */
    private String temp2;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;
}

