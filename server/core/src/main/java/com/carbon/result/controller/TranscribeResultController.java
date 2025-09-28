package com.carbon.result.controller;

import com.carbon.common.BaseResponse;
import com.carbon.common.ResultUtils;
import com.carbon.result.model.vo.TranscribeResultVO;
import com.carbon.result.service.TranscribeResultService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 转录结果Controller
 */
@RestController
@RequestMapping("/core/result")
public class TranscribeResultController {

    private final TranscribeResultService transcribeResultService;

    public TranscribeResultController(TranscribeResultService transcribeResultService) {
        this.transcribeResultService = transcribeResultService;
    }

    /**
     * 根据音频id查询转录结果
     * @param audioId 音频id
     * @return List<TranscribeResultVO>
     */
    @GetMapping("/list/{audioId}")
    public BaseResponse<List<TranscribeResultVO>> list(@PathVariable("audioId") Long audioId) {
        return ResultUtils.success(transcribeResultService.listByAudioId(audioId));
    }

}


