package com.carbon.audio.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class AudioFileUpdateRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     * 文件名
     */
    private String fileName;


}


