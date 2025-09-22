package com.carbon.audio.controller;

import com.carbon.annotation.Log;
import com.carbon.common.BaseResponse;
import com.carbon.common.BusinessType;
import com.carbon.common.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 音频文件Controller
 */
@RestController
@RequestMapping("/core/audio")
public class AudioFilesController {

    @GetMapping("/list")
    @Log(title = "查询音频文件列表", businessType = BusinessType.OTHER)
    public BaseResponse<String> list() {
        return ResultUtils.success("success");
    }
}
