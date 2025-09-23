package com.carbon.audio.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.carbon.audio.model.dto.AudioFileAddRequest;
import org.apache.ibatis.annotations.Mapper;
import com.carbon.audio.model.entity.AudioFile;

/**
 * 音频文件Mapper接口
 */
@Mapper
public interface AudioFileMapper extends BaseMapper<AudioFile> {
    /**
     * 查询音频文件
     *
     * @param id 音频文件主键
     * @return 音频文件
     */
    AudioFile selectAudioFileById(Long id);

    /**
     * 查询音频文件列表
     *
     * @param audioFile 音频文件
     * @return 音频文件集合
     */
    List<AudioFile> selectAudioFileList(AudioFile audioFile);

    /**
     * 新增音频文件
     *
     * @param audioFile 音频文件
     * @return 结果
     */
    int insertAudioFile(AudioFile audioFile);

    /**
     * 修改音频文件
     *
     * @param audioFile 音频文件
     * @return 结果
     */
    int updateAudioFile(AudioFile audioFile);

    /**
     * 删除音频文件
     *
     * @param id 音频文件主键
     * @return 结果
     */
    int deleteAudioFileById(Long id);

    /**
     * 批量删除音频文件
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteAudioFileByIds(Long[] ids);

    /**
     * 批量逻辑删除音频文件
     *
     * @param ids 音频文件主键
     * @return 结果
     */
    int logicRemoveByIds(List<Long> ids);

    /**
     * 通过音频文件主键id逻辑删除信息
     *
     * @param id 音频文件主键
     * @return 结果
     */
    int logicRemoveById(Long id);
}
