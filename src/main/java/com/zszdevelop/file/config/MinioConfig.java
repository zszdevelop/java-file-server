package com.zszdevelop.file.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * minio配置
 *
 * @author zsz
 * @date 2021-29-29
 */
@Slf4j
@Configuration
public class MinioConfig {

    @Resource
    private MinioProperties minioProperties;

    @Bean
    public MinioClient minioClient() {
        // 使用MinIO服务的URL，端口，Access key和Secret key创建一个MinioClient对象
        MinioClient minioClient =
                MinioClient.builder()
                        .endpoint(minioProperties.getEndpoint())
                        .credentials(minioProperties.getAccessKey(),
                                minioProperties.getSecretKey())
                        .build();


        // 检查存储桶是否已经存在
        try {
            String bucketName = minioProperties.getBucketName();
            BucketExistsArgs bucketExistsArgs = new BucketExistsArgs.Builder().bucket(bucketName).build();
            boolean isExist = minioClient.bucketExists(bucketExistsArgs);
            if(isExist) {
                log.info("minio Bucket already exists.");
            } else {
                // 创建一个名为asiatrip的存储桶，用于存储照片的zip文件。
                MakeBucketArgs makeBucketArgs = new MakeBucketArgs.Builder().bucket(bucketName).build();
                minioClient.makeBucket(makeBucketArgs);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("minio 创建 bucket 失败");
        }

        return minioClient;
    }
}