package com.carbon.audio.service.mq;


import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.carbon.audio.model.entity.AudioFile;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AudioProducer {

    @Value("${com.carbon.minio.endPoint}")
    private String endPoint;
    @Value("${com.carbon.minio.bucketName}")
    private String bucketName;

    public static final String TOPIC = "asr_transfer_topic";

    private static final String TAG = "tag_asr_transfer_txt";

    private final RocketMQTemplate rocketMQTemplate;

    public AudioProducer(RocketMQTemplate rocketMQTemplate) {
        this.rocketMQTemplate = rocketMQTemplate;
    }

    /**
     * 发送音频转录任务消息
     *
     * @param audioFile 音频文件信息
     */
    public void sendAudioInfo(AudioFile audioFile) {
        audioFile.setFilePath(endPoint + "/" + bucketName + "/" + audioFile.getFilePath());
        rocketMQTemplate.convertAndSend(TOPIC + ":" + TAG, JSONUtil.toJsonStr(audioFile));
    }
}
