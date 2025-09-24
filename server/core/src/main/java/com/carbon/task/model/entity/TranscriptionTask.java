package com.carbon.task.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 转写任务对象 transcription_task
 */
@Data
public class TranscriptionTask implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;


    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 音频文件 id
     */
    private Long fileId;

    /**
     * 模型名称
     */
    private String modelName;

    /**
     * 接口地址
     */
    private String interfaceAddress;

    /**
     * 执行时间
     */
    private Long executionTime;

    /**
     * 任务状态（0 - 待处理、1 - 处理中、2 - 处理成功、3 - 处理失败）
     */
    private Long status;

    /**
     * 结果
     */
    private String result;

    /**
     * 保留字段1
     */
    private String temp1;

    /**
     * 保留字段2
     */
    private String temp2;

    /**
     * 保留字段3
     */
    private String temp3;

    /**
     * 是否删除
     */
    private Long isDelete;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private String updateTime;

    /**
     * 主键集合
     */
    @TableField(exist = false)
    private List<Long> ids;
}
