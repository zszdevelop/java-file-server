package com.zszdevelop.file.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "file-server.minio")
public class MinioProperties {

    /**
     * minio地址
     */
    private String endpoint;
    /**
     * minio 账号
     */
    private String accessKey;
    /**
     * minio 密码
     */
    private String secretKey;
    /**
     * 存储的bucket名称
     */
    private String bucketName;
    /**
     * 预览过期时间
     */
    private Integer expiry = 60;
}
