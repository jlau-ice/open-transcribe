package com.carbon.audio.model.dto;

import com.carbon.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public class AudioFileQueryRequest extends PageRequest implements Serializable {

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
     * 文件类型(wav,mp3..)
     */
    private String fileType;

    /**
     * 文件状态（0 - 待处理、1 - 处理中、2 - 处理成功、3 - 处理失败）
     */
    private Integer status;

}


