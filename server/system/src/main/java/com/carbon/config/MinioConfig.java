package com.carbon.config;

import io.minio.MinioClient;
import io.minio.errors.MinioException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Minio配置
 **/
@Slf4j
@Configuration
public class MinioConfig {

    @Value("${com.carbon.minio.endPoint}")
    private String endPoint;
    @Value("${com.carbon.minio.accessKey}")
    private String accessKey;
    @Value("${com.carbon.minio.secretKey}")
    private String secretKey;
    @Value("${com.carbon.minio.bucketName}")
    private String bucketName;

    /**
     * 创建MinioClient
     *
     * @return MinioClient
     * @throws Exception 创建失败
     */
    @Bean
    public MinioClient minioClient() throws Exception {
        MinioClient client = MinioClient.builder()
                .endpoint(endPoint)
                .credentials(accessKey, secretKey)
                .build();
        if (StringUtils.isBlank(bucketName)) {
            log.error("存储桶名称缺失，请在yaml文件中进行完善！");
            throw new MinioException("存储桶名称缺失，请在yaml文件中进行完善！");
        }
        log.info("MINIO:地址={},存储桶名={}", endPoint, bucketName);
        return client;
    }
}
