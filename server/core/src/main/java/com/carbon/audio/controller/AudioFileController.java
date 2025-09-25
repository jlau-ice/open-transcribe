package com.carbon.audio.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carbon.annotation.Log;
import com.carbon.audio.model.dto.AudioFileQueryRequest;
import com.carbon.audio.model.vo.AudioFileVO;
import com.carbon.audio.service.AudioFileService;
import com.carbon.common.BaseResponse;
import com.carbon.common.BusinessType;
import com.carbon.common.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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

    /**
     * 新增音频文件
     *
     * @param file    文件
     * @param request cookie
     */
    @PostMapping("/upload")
    @Log(title = "上传音频文件", businessType = BusinessType.INSERT)
    public BaseResponse<AudioFileVO> upload(@RequestPart("file") MultipartFile file, @ApiIgnore HttpServletRequest request) {
        return ResultUtils.success(audioFileService.addAudioFile(file, request));
    }


    /**
     * 获取所有文件
     *
     * @param request query
     * @return List<AudioFileVO>
     */
    @PostMapping("/list/vo")
    @Log(title = "获取所有文件", businessType = BusinessType.SELECT)
    public BaseResponse<List<AudioFileVO>> listAudioFile(@RequestBody AudioFileQueryRequest request) {
        List<AudioFileVO> res = audioFileService.listAudioFile(request);
        return ResultUtils.success(res);
    }

    /**
     * 分页查询音频文件
     *
     * @param request query
     * @return Page<AudioFileVO>
     */
    @PostMapping("/list/page/vo")
    @Log(title = "获取文件带分页", businessType = BusinessType.SELECT)
    public BaseResponse<Page<AudioFileVO>> listAudioFileByPage(@RequestBody AudioFileQueryRequest request) {
        Page<AudioFileVO> page = audioFileService.listAudioFileByPage(request);
        return ResultUtils.success(page);
    }

    /**
     * 根据id查询音频文件
     *
     * @param id id
     * @return AudioFileVO
     */
    @GetMapping("/get/vo/{id}")
    @Log(title = "获取文件", businessType = BusinessType.SELECT)
    public BaseResponse<AudioFileVO> getAudioFileVOById(@PathVariable("id") Long id) {
        AudioFileVO res = audioFileService.selectAudioFileVOById(id);
        return ResultUtils.success(res);
    }


    /**
     * 删除音频文件
     *
     * @param id 音频文件id
     * @return 删除结果
     */
    @DeleteMapping("/delete/{id}")
    @Log(title = "删除音频文件", businessType = BusinessType.DELETE)
    public BaseResponse<String> deleteAudioFile(@PathVariable("id") Long id) {
        audioFileService.deleteById(id);
        return ResultUtils.success();
    }

}
