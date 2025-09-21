package com.carbon.service;

import com.carbon.model.dto.minio.MinioInfo;
import io.minio.errors.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * 服务器文件管理
 */
public interface MinioService {

    MinioInfo upload(MultipartFile file, String filePath);

    InputStream getMinioInputStream(String filePath) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

    boolean delete(String bucketName, String fileName);

    MinioInfo download(String bucketName, String fileName, HttpServletResponse response);

    boolean allDelete(String bucketName, String fileName, Long sysFileId);
}
