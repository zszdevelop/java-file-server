package com.zszdevelop.file.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 文件服务基础配置
 * @author zsz
 * @date 2021-11-11
 */
@Component
@ConfigurationProperties(prefix = "file-server.base")
public class FileBaseConfig {

    /**
     * 文件大小限制
     * 默认大小 50M
     */
    public static long maxSize = 50 * 1024 * 1024;

    /**
     * 文件名最大长度
     * 默认为100
     * */
    private static int fileNameMaxlength;

    public static long getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(long maxSize) {
        FileBaseConfig.maxSize = maxSize;
    }

    public static   int getFileNameMaxlength() {
        if (fileNameMaxlength == 0){
            fileNameMaxlength = 100;
        }
        return fileNameMaxlength;
    }

    public  void setFileNameMaxlength(int fileNameMaxlength) {
        FileBaseConfig.fileNameMaxlength = fileNameMaxlength;
    }


}
