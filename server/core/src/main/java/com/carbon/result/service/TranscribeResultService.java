package com.carbon.result.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.carbon.result.model.entity.TranscribeResult;
import com.carbon.result.model.vo.TranscribeResultVO;

import java.util.List;

public interface TranscribeResultService extends IService<TranscribeResult> {

    /**
     * 根据音频id查询转录结果
     * @param audioId 音频id
     * @return List<TranscribeResultVO>
     */
    List<TranscribeResultVO> listByAudioId(Long audioId);
}
