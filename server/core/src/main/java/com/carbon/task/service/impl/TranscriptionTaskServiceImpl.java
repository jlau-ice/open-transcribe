package com.carbon.task.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.carbon.task.mapper.TranscriptionTaskMapper;
import com.carbon.task.model.entity.TranscriptionTask;
import com.carbon.task.service.TranscriptionTaskService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 转写任务Service业务层处理
 */
@Service
@Transactional
public class TranscriptionTaskServiceImpl implements TranscriptionTaskService {
    @Autowired
    private TranscriptionTaskMapper transcriptionTaskMapper;

    /**
     * 查询转写任务
     *
     * @param id 转写任务主键
     * @return 转写任务
     */
    @Override
    public TranscriptionTask selectTranscriptionTaskById(Long id) {
        return transcriptionTaskMapper.selectTranscriptionTaskById(id);
    }

    /**
     * 查询转写任务列表
     *
     * @param transcriptionTask 转写任务
     * @return 转写任务
     */
    @Override
    public List<TranscriptionTask> selectTranscriptionTaskList(TranscriptionTask transcriptionTask) {
        return transcriptionTaskMapper.selectTranscriptionTaskList(transcriptionTask);
    }

    /**
     * 新增转写任务
     *
     * @param transcriptionTask 转写任务
     * @return 结果
     */
    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public int insertTranscriptionTask(TranscriptionTask transcriptionTask) {
        return transcriptionTaskMapper.insertTranscriptionTask(transcriptionTask);
    }

    /**
     * 修改转写任务
     *
     * @param transcriptionTask 转写任务
     * @return 结果
     */
    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public int updateTranscriptionTask(TranscriptionTask transcriptionTask) {

        return transcriptionTaskMapper.updateTranscriptionTask(transcriptionTask);
    }

    /**
     * 批量删除转写任务
     *
     * @param ids 需要删除的转写任务主键
     * @return 结果
     */
    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public int deleteTranscriptionTaskByIds(List<Long> ids) {
        return transcriptionTaskMapper.logicRemoveByIds(ids);
        //return transcriptionTaskMapper.deleteTranscriptionTaskByIds(ids);
    }

    /**
     * 删除转写任务信息
     *
     * @param id 转写任务主键
     * @return 结果
     */
    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public int deleteTranscriptionTaskById(Long id) {
        return transcriptionTaskMapper.logicRemoveById(id);
        //return transcriptionTaskMapper.deleteTranscriptionTaskById(id);
    }

}
