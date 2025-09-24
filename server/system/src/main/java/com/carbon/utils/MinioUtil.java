package com.carbon.utils;

import com.carbon.model.dto.minio.MinioInfo;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class MinioUtil {

    @Value("${com.carbon.minio.bucketName}")
    private String bucketName;

    private final MinioClient minioClient;


    @Autowired
    public MinioUtil(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    /**
     * 初始化桶
     */
    @SneakyThrows
    private void initBucket() {
        boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!bucketExists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }


    private String buildOutFileName(String originalFileName, String filePath) {
        //输出文件名（路径）处理
        String outFileNameBuilder = filePath + "/" + originalFileName;
        //获取上传后的文件名 涵路径
        return FileUtils.formatFileName(bucketName, outFileNameBuilder);
    }


    @SneakyThrows
    private MinioInfo minioUpload(String originalFileName, String filePath, InputStream stream) {
        //获取上传后的文件名 仅文件 不涵路径
        String objectName = buildOutFileName(originalFileName, filePath);
        //获取文件大小
        long fileSize = stream.available();
        //1 初始化bucket
        initBucket();
        //2 文件上传  参数校验 和分片上传
        PutObjectArgs objectArgs = PutObjectArgs.builder().object(objectName)
                .bucket(bucketName)
                .stream(stream, fileSize, -1).build();
        minioClient.putObject(objectArgs);
        return new MinioInfo(bucketName, objectName, FileUtils.formatFileSize(fileSize));
    }


    @SneakyThrows
    public MinioInfo upload(MultipartFile multipartFile, String filePath) {
        //对文件进行上传
        return minioUpload(multipartFile.getOriginalFilename(), filePath, multipartFile.getInputStream());
    }

    @SneakyThrows
    public MinioInfo upload(String base64Str, String originalFileName, String filePath) {
        //判断base64数据是否为空
        if (StringUtils.isNotBlank(base64Str)) {
            String base65StrLast = base64Str.substring(base64Str.indexOf("base64,") + 7);
            byte[] byt = Base64.decodeBase64(base65StrLast);
            for (int i = 0; i < byt.length; ++i) {
                if (byt[i] < 0) {
                    byt[i] += (byte) 256;
                }
            }
            //字节转换为输入流
            InputStream stream = new ByteArrayInputStream(byt);
            return minioUpload(originalFileName, filePath, stream);
        }
        return null;
    }


    @SneakyThrows
    public void removeObject(String objectName) {
        RemoveObjectArgs removeBucketArgs = RemoveObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName).build();
        minioClient.removeObject(removeBucketArgs);
    }

    @SneakyThrows
    public List<String> removeObjects(List<String> objectNames) {
        List<String> deleteErrors = new ArrayList<>();
        List<DeleteObject> deleteObjects = objectNames.stream().map(DeleteObject::new).collect(Collectors.toList());
        //对上传异常反馈进行记录
        Iterable<Result<DeleteError>> results =
                minioClient.removeObjects(
                        RemoveObjectsArgs
                                .builder()
                                .bucket(bucketName)
                                .objects(deleteObjects)
                                .build());
        for (Result<DeleteError> result : results) {
            DeleteError error = result.get();
            deleteErrors.add(error.objectName());
        }
        return deleteErrors;
    }

    public boolean checkFileIsExist(String objectName) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder().bucket(bucketName).object(objectName).build()
            );
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @SneakyThrows
    public String presignedGetObject(String objectName, Integer expires) {
        if (checkFileIsExist(objectName)) {
            if (expires == null) {
                expires = 86400;
            }
            GetPresignedObjectUrlArgs getPresignedObjectUrlArgs = GetPresignedObjectUrlArgs
                    .builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .expiry(expires)// 单位秒
                    .method(Method.GET)
                    .build();
            return minioClient.getPresignedObjectUrl(getPresignedObjectUrlArgs);
        }
        return null;
    }

    public boolean statObject(String objectName) {
        StatObjectArgs statObjectArgs = StatObjectArgs
                .builder()
                .bucket(bucketName)
                .object(objectName).build();
        try {
            //文件是否存在  文件不存在会抛出异常 -- 官方解释
            minioClient.statObject(statObjectArgs);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @SneakyThrows
    public List<MinioInfo> listObjects() {
        ListObjectsArgs listObjectsArgs = ListObjectsArgs
                .builder()
                .recursive(true)//递归查询多层级元素
                .bucket(bucketName)
                .build();
        //.startAfter("2021")
        //.prefix("2") // 指定前缀
        //.maxKeys(100) // 最大数量
        Iterable<Result<Item>> objects = minioClient.listObjects(listObjectsArgs);
        List<MinioInfo> itemList = new ArrayList<MinioInfo>();
        for (Result<Item> object : objects) {
            Item item = object.get();
            MinioInfo info = new MinioInfo(bucketName, item.objectName(), FileUtils.formatFileSize(item.size()));
            itemList.add(info);
        }
        return itemList;
    }

    @SneakyThrows
    public String getBase64File(String objectName) {
        GetObjectArgs getObjectArgs = GetObjectArgs
                .builder()
                .bucket(bucketName)
                .object(objectName)
                .build();
        InputStream stream = minioClient.getObject(getObjectArgs);
        // 读取图片字节数组
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        while ((rc = stream.read(buff, 0, 100)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        stream.close();
        // 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        byte[] data = swapStream.toByteArray();
        return new String(Base64.encodeBase64(data));
    }

    @SneakyThrows
    public String getFileUrl(String objectName) {
        if (checkFileIsExist(objectName)) {//新版minio获取图片路径，路径是否存在图片没有区分，故获取前判断minio是否存在此文件
            GetPresignedObjectUrlArgs getPresignedObjectUrlArgs = GetPresignedObjectUrlArgs
                    .builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .expiry(60 * 60 * 24 * 7)// 单位秒
                    .method(Method.GET)
                    .build();
            return minioClient.getPresignedObjectUrl(getPresignedObjectUrlArgs);
        }
        return null;
    }


}


