package com.carbon.task.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.carbon.task.model.entity.TranscriptionTask;

/**
 * 转写任务Mapper接口
 */
@Mapper
public interface TranscriptionTaskMapper extends BaseMapper<TranscriptionTask> {
    /**
     * 查询转写任务
     *
     * @param id 转写任务主键
     * @return 转写任务
     */
    TranscriptionTask selectTranscriptionTaskById(Long id);

    /**
     * 查询转写任务列表
     *
     * @param transcriptionTask 转写任务
     * @return 转写任务集合
     */
    List<TranscriptionTask> selectTranscriptionTaskList(TranscriptionTask transcriptionTask);

    /**
     * 新增转写任务
     *
     * @param transcriptionTask 转写任务
     * @return 结果
     */
    int insertTranscriptionTask(TranscriptionTask transcriptionTask);

    /**
     * 修改转写任务
     *
     * @param transcriptionTask 转写任务
     * @return 结果
     */
    int updateTranscriptionTask(TranscriptionTask transcriptionTask);

    /**
     * 删除转写任务
     *
     * @param id 转写任务主键
     * @return 结果
     */
    int deleteTranscriptionTaskById(Long id);

    /**
     * 批量删除转写任务
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteTranscriptionTaskByIds(Long[] ids);

    /**
     * 批量逻辑删除转写任务
     *
     * @param ids 转写任务主键
     * @return 结果
     */
    int logicRemoveByIds(List<Long> ids);

    /**
     * 通过转写任务主键id逻辑删除信息
     *
     * @param id 转写任务主键
     * @return 结果
     */
    int logicRemoveById(Long id);
}
