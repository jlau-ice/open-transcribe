package com.carbon.audio.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 音频文件对象 audio_file
 */
@Data
@TableName("audio_file")
public class AudioFile implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件大小(kb)
     */
    private Long fileSize;

    /**
     * 音频时长
     */
    private Long duration;

    /**
     * 文件类型(wav,mp3..)
     */
    private String fileType;

    /**
     * 文件状态（0 - 待处理、1 - 处理中、2 - 处理成功、3 - 处理失败）
     */
    private Integer status;

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
    private Integer isDelete;
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
