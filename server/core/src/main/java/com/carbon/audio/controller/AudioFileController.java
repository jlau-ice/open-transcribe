package com.carbon.audio.controller;

import com.carbon.annotation.Log;
import com.carbon.audio.service.AudioFileService;
import com.carbon.common.BaseResponse;
import com.carbon.common.BusinessType;
import com.carbon.common.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;

/**
 * 音频文件Controller
 */
@RestController
@RequestMapping("/core/audio")
public class AudioFileController {

    private final AudioFileService audioFileService;

    @Autowired
    public AudioFileController(AudioFileService audioFileService) {
        this.audioFileService = audioFileService;
    }

    @PostMapping("/upload")
    @Log(title = "上传音频文件", businessType = BusinessType.INSERT)
    public BaseResponse<String> upload(@RequestPart("file") MultipartFile file, @ApiIgnore HttpServletRequest request) {
        audioFileService.addAudioFile(file, request);
        return ResultUtils.success();
    }
}
