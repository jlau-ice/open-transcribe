package com.carbon.audio.service.impl;

import java.util.List;

import com.carbon.audio.model.entity.AudioFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.carbon.audio.mapper.AudioFileMapper;
import com.carbon.audio.service.AudioFileService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 音频文件Service业务层处理
 *
 * @author jkr
 * @date 2025-09-22
 */
@Service
@Transactional
public class AudioFileServiceImpl implements AudioFileService {
    @Autowired
    private AudioFileMapper audioFileMapper;

    /**
     * 查询音频文件
     *
     * @param id 音频文件主键
     * @return 音频文件
     */
    @Override
    public AudioFile selectAudioFilesById(Long id) {
        return audioFileMapper.selectAudioFileById(id);
    }

    /**
     * 查询音频文件列表
     *
     * @param audioFile 音频文件
     * @return 音频文件
     */
    @Override
    public List<AudioFile> selectAudioFilesList(AudioFile audioFile) {
        return audioFileMapper.selectAudioFileList(audioFile);
    }

    /**
     * 新增音频文件
     *
     * @param audioFile 音频文件
     * @return 结果
     */
    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public int insertAudioFiles(AudioFile audioFile) {
        return audioFileMapper.insertAudioFile(audioFile);
    }

    /**
     * 修改音频文件
     *
     * @param audioFile 音频文件
     * @return 结果
     */
    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public int updateAudioFiles(AudioFile audioFile) {
        return audioFileMapper.updateAudioFile(audioFile);
    }

    /**
     * 批量删除音频文件
     *
     * @param ids 需要删除的音频文件主键
     * @return 结果
     */
    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public int deleteAudioFilesByIds(List<Long> ids) {
        return audioFileMapper.logicRemoveByIds(ids);
        //return audioFilesMapper.deleteAudioFilesByIds(ids);
    }

    /**
     * 删除音频文件信息
     *
     * @param id 音频文件主键
     * @return 结果
     */
    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public int deleteAudioFilesById(Long id) {
        return audioFileMapper.logicRemoveById(id);
        //return audioFilesMapper.deleteAudioFilesById(id);
    }

}
