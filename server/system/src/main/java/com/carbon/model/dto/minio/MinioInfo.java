package com.carbon.model.dto.minio;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.InputStream;
import java.io.OutputStream;

@Data
public class MinioInfo {

    @ApiModelProperty(name = "bucketName", value = "存储桶名称")
    private String bucketName;

    @ApiModelProperty(name = "fileSize", value = "文件大小")
    private String fileSize;

    @ApiModelProperty(name = "fileName", value = "拼接路径的文件名")
    private String fileName;

    private String url;
    @JsonIgnore
    private InputStream in;
    @JsonIgnore
    private OutputStream out;

    public MinioInfo(String bucketName, String fileName, String fileSize) {
        this.bucketName = bucketName;
        this.fileName = fileName;
        this.url = fileName;
        this.fileSize = fileSize;
    }

    public MinioInfo(InputStream in, OutputStream out) {
        this.in = in;
        this.out = out;
    }
}
