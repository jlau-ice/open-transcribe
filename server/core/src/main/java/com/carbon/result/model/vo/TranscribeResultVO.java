package com.carbon.result.model.vo;


import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.carbon.result.model.dto.TranscriptSegment;
import com.carbon.result.model.entity.TranscribeResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private List<TranscriptSegment> resultSegments;

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
        String resultText = transcribeResult.getResultText();
        List<TranscriptSegment> segments;
        segments = JSONUtil.toList(resultText, TranscriptSegment.class);
        transcribeResultVO.setResultSegments(segments);
        return transcribeResultVO;
    }

}

