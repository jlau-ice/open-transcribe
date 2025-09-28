package com.carbon.mq;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.carbon.audio.model.entity.AudioFile;
import com.carbon.audio.service.AudioFileService;
import com.carbon.result.model.dto.TranscribeMessage;
import com.carbon.result.model.entity.TranscribeResult;
import com.carbon.result.service.TranscribeResultService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RocketMQMessageListener(
        topic = "asr_result_topic",
        selectorExpression = "tag_asr_transfer_result",
        consumerGroup = "asr_transfer_consumer_group"
)
public class AudioConsumer implements RocketMQListener<String> {

    private final TranscribeResultService transcribeResultService;

    private final AudioFileService audioFileService;

    public AudioConsumer(TranscribeResultService transcribeResultService, AudioFileService audioFileService) {
        this.transcribeResultService = transcribeResultService;
        this.audioFileService = audioFileService;
    }

    @Override
    public void onMessage(String message) {
        log.info("收到音频转录任务消息：{}", message);
        try {
            TranscribeMessage transcribeMessage = JSONUtil.toBean(message, TranscribeMessage.class);
            TranscribeResult transcribeResult = new TranscribeResult();
            BeanUtils.copyProperties(transcribeMessage, transcribeResult);
            boolean save = transcribeResultService.save(transcribeResult);
            if (save) {
                log.info("保存音频转录结果成功：{}", transcribeResult);
                LambdaUpdateWrapper<AudioFile> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(AudioFile::getId, transcribeMessage.getAudioId())
                        .set(AudioFile::getStatus, transcribeResult.getStatus().equals("success") ? 2 : 3);
                audioFileService.update(null, updateWrapper);
            }
        } catch (Exception e) {
            log.error("消息解析失败: {}", message, e);
        }
    }
}


