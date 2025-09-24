package com.carbon.audio.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.carbon.audio.model.dto.AudioFileQueryRequest;
import com.carbon.audio.model.entity.AudioFile;
import com.carbon.audio.model.vo.AudioFileVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 音频文件Service接口
 */
public interface AudioFileService extends IService<AudioFile> {


    /**
     * 新增音频文件
     *
     * @param file    文件
     * @param request cookie
     */
    void addAudioFile(MultipartFile file, HttpServletRequest request);

    /**
     * 分页查询音频文件
     * @param request query
     * @return Page<AudioFileVO>
     */
    Page<AudioFileVO> listAudioFileByPage(AudioFileQueryRequest request);

    /**
     * 获取所有文件
     * @param request query
     * @return List<AudioFileVO>
     */
    List<AudioFileVO> listAudioFile(AudioFileQueryRequest request);

    /**
     * 获取查询条件
     *
     * @param audioFileQueryRequest 查询实体
     * @return QueryWrapper<AudioFile> 条件Wrapper
     */
    QueryWrapper<AudioFile> getQueryWrapper(AudioFileQueryRequest audioFileQueryRequest);

    /**
     * 查询音频文件
     *
     * @param id 音频文件主键
     * @return 音频文件
     */
    AudioFile selectAudioFilesById(Long id);

    /**
     * 查询音频文件列表
     *
     * @param audioFile 音频文件
     * @return 音频文件集合
     */
    List<AudioFile> selectAudioFilesList(AudioFile audioFile);

    /**
     * 新增音频文件
     *
     * @param audioFile 音频文件
     * @return 结果
     */
    int insertAudioFiles(AudioFile audioFile);

    /**
     * 修改音频文件
     *
     * @param audioFile 音频文件
     * @return 结果
     */
    int updateAudioFiles(AudioFile audioFile);

    /**
     * 批量删除音频文件
     *
     * @param ids 需要删除的音频文件主键集合
     * @return 结果
     */
    int deleteAudioFilesByIds(List<Long> ids);

    /**
     * 删除音频文件信息
     *
     * @param id 音频文件主键
     * @return 结果
     */
    int deleteAudioFilesById(Long id);

}
