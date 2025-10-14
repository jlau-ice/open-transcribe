package com.carbon.mq;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.carbon.audio.model.entity.AudioFile;
import com.carbon.audio.service.AudioFileService;
import com.carbon.common.AsyncTaskService;
import com.carbon.common.UserContext;
import com.carbon.model.entity.User;
import com.carbon.result.mapper.TranscribeResultMapper;
import com.carbon.result.model.dto.TranscribeMessage;
import com.carbon.result.model.entity.TranscribeResult;
import com.carbon.result.service.TranscribeResultService;
import com.carbon.websocket.model.SuccessNotification;
import com.carbon.websocket.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RocketMQMessageListener(
        topic = "asr_result_topic",
        selectorExpression = "tag_asr_transfer_result",
        consumerGroup = "asr_transfer_consumer_group"
)
public class AudioConsumer implements RocketMQListener<String> {

    private final TranscribeResultService transcribeResultService;

    private final TranscribeResultMapper transcribeResultMapper;

    private final AudioFileService audioFileService;

    private final WebSocketService webSocketService;

    private final AsyncTaskService asyncTaskService;

    public AudioConsumer(TranscribeResultService transcribeResultService,
                         AudioFileService audioFileService,
                         TranscribeResultMapper transcribeResultMapper,
                         WebSocketService webSocketService,
                         AsyncTaskService asyncTaskService) {
        this.transcribeResultService = transcribeResultService;
        this.audioFileService = audioFileService;
        this.transcribeResultMapper = transcribeResultMapper;
        this.webSocketService = webSocketService;
        this.asyncTaskService = asyncTaskService;
    }

    @Override
    public void onMessage(String message) {
        log.info("收到音频转录任务消息：{}", message);
        try {
            TranscribeMessage transcribeMessage = JSONUtil.toBean(message, TranscribeMessage.class);
            TranscribeResult transcribeResult = new TranscribeResult();
            BeanUtils.copyProperties(transcribeMessage, transcribeResult);
            LambdaQueryWrapper<TranscribeResult> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TranscribeResult::getAudioId, transcribeMessage.getAudioId());
            TranscribeResult existingResult = transcribeResultMapper.selectOne(queryWrapper);
            boolean save;
            if (existingResult != null) {
                transcribeResult.setId(existingResult.getId());
                save = transcribeResultService.updateById(transcribeResult);
            } else {
                save = transcribeResultService.save(transcribeResult);
            }
            if (save) {
                log.info("保存音频转录结果成功：{}", transcribeResult);
                LambdaUpdateWrapper<AudioFile> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(AudioFile::getId, transcribeMessage.getAudioId())
                        .set(AudioFile::getStatus, transcribeResult.getStatus().equals("success") ? 2 : 3);
                audioFileService.update(null, updateWrapper);
                // socket 通知 转录完成

                AudioFile audioFile = audioFileService.getById(transcribeMessage.getAudioId());
                SuccessNotification successNotification = new SuccessNotification();
                successNotification.setAudioId(transcribeMessage.getAudioId());
                successNotification.setStatus("success");
                successNotification.setMessage("转录成功");
                webSocketService.sendOneMessage(String.valueOf(audioFile.getUserId()), JSONUtil.toJsonStr(successNotification));
            }
        } catch (Exception e) {
            log.error("消息解析失败: {}", message, e);
        }
    }
}


