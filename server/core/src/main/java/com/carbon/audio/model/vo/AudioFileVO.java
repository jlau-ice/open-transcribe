package com.carbon.audio.model.vo;

import com.carbon.audio.model.entity.AudioFile;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 音频文件对象 audio_file
 */
@Data
public class AudioFileVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
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
     * 文件状态（0 - 待处理、1 - 处理中、2 - 处理成功、3 - 处理失败）
     */
    private Integer status;

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
     * 对象转包装类
     *
     * @param audioFile entity
     * @return AudioFileVO vo
     */
    public static AudioFileVO objToVo(AudioFile audioFile) {
        AudioFileVO audioFileVO = new AudioFileVO();
        BeanUtils.copyProperties(audioFile, audioFileVO);
        return audioFileVO;
    }

}
