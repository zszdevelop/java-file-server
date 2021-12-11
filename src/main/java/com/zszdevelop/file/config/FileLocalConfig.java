package com.zszdevelop.file.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 文件服务基础配置
 * @author zsz
 * @date 2021-11-11
 */
@Data
@Component
@ConfigurationProperties(prefix = "file-server.local")
public class FileLocalConfig {

    /**
     * 默认存储路径
     */
    private  String baseDir ;

}
