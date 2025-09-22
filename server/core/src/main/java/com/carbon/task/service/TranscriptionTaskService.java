package com.carbon.task.service;

import java.util.List;

import com.carbon.task.model.entity.TranscriptionTask;

/**
 * 转写任务Service接口
 */
public interface TranscriptionTaskService {
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
     * 批量删除转写任务
     *
     * @param ids 需要删除的转写任务主键集合
     * @return 结果
     */
    int deleteTranscriptionTaskByIds(List<Long> ids);

    /**
     * 删除转写任务信息
     *
     * @param id 转写任务主键
     * @return 结果
     */
    int deleteTranscriptionTaskById(Long id);

}
