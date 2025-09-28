package com.carbon.result.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.carbon.result.mapper.TranscribeResultMapper;
import com.carbon.result.model.entity.TranscribeResult;
import com.carbon.result.model.vo.TranscribeResultVO;
import com.carbon.result.service.TranscribeResultService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TranscribeResultServiceImpl extends ServiceImpl<TranscribeResultMapper, TranscribeResult> implements TranscribeResultService {

    /**
     * 根据音频id查询转录结果
     *
     * @param audioId 音频id
     * @return List<TranscribeResultVO>
     */
    @Override
    public List<TranscribeResultVO> listByAudioId(Long audioId) {
        LambdaQueryWrapper<TranscribeResult> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TranscribeResult::getAudioId, audioId);
        return this.list(queryWrapper).stream().map(TranscribeResultVO::objToVo).toList();
    }
}
