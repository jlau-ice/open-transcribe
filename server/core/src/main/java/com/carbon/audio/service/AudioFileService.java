package com.carbon.audio.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.carbon.audio.model.entity.AudioFile;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 音频文件Service接口
 */
public interface AudioFileService extends IService<AudioFile> {


    void addAudioFile(MultipartFile file, HttpServletRequest request);
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
