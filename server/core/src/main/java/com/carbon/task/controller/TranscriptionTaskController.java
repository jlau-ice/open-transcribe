package com.carbon.task.controller;

import com.carbon.common.BaseResponse;
import com.carbon.common.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 转写任务Controller
 */
@RestController
@RequestMapping("/core/task")
public class TranscriptionTaskController {
    @GetMapping("/list" )
    public BaseResponse<String> list() {
        return ResultUtils.success("success");
    }
}
