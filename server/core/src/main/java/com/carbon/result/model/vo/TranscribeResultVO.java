package com.carbon.result.model.vo;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.carbon.result.model.entity.TranscribeResult;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;

@Data
public class TranscribeResultVO {

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
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 对象转包装类
     * @param transcribeResult  entity
     * @return TranscribeResultVO vo
     */
    public static TranscribeResultVO objToVo(TranscribeResult transcribeResult) {
        TranscribeResultVO transcribeResultVO = new TranscribeResultVO();
        BeanUtils.copyProperties(transcribeResult, transcribeResultVO);
        return transcribeResultVO;
    }

}

