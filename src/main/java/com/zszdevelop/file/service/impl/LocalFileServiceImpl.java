package com.zszdevelop.file.service.impl;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.lang.UUID;
import com.zszdevelop.file.config.FileBaseConfig;
import com.zszdevelop.file.config.FileLocalConfig;
import com.zszdevelop.file.domian.FileInfo;
import com.zszdevelop.file.domian.FileResult;
import com.zszdevelop.file.service.LocalFileService;
import com.zszdevelop.file.utils.FileUploadUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author zsz
 * @date 2021-11-11
 */
@Slf4j
//@Primary
@Service
public class LocalFileServiceImpl implements LocalFileService {

    /**
     * 资源映射路径 前缀
     */
//    @Value("${file-server.local.prefix}")
//    public String localFilePrefix;

    /**
     * 域名或本机访问地址
     */
//    @Value("${file-server.local.domain}")
//    public String domain;

    /**
     * 上传文件存储在本地的根路径
     */
//    @Value("${file-server.local.path}")
//    private String localFilePath;

    @Resource
    FileLocalConfig fileLocalConfig;

    @Override
    public FileResult<InputStream> download(String fileName) {
        return null;
    }

    @Override
    public FileResult<FileInfo> upload(MultipartFile file, String filename) {

        FileResult<FileInfo> checkResult = FileUploadUtils.check(file,filename);
        if (!checkResult.isSuccess()) {
            return checkResult;
        }
        try {
            File desc = getAbsoluteFile(fileLocalConfig.getBaseDir(), filename);
            file.transferTo(desc);
        } catch (IOException e) {
            log.error("FileService  local文件上传失败{}", e.getMessage(), e);
            return FileResult.error("local文件上传失败");
        }

        FileInfo fileInfo = new FileInfo();
        fileInfo.setPath(filename);
        return FileResult.success(fileInfo);
    }



    @Override
    public FileResult<FileInfo> upload(File file, String filename) {
        return null;
    }

    @Override
    public FileResult<FileInfo> copy(String sourcePath, String targetPath) {
        return null;
    }

    @Override
    public FileResult delete(String fileName) {
        return null;
    }

    @Override
    public boolean exists(String fileName) {
        return false;
    }

    @Override
    public String getPreviewUrl(String fileName) {
        return null;
    }


    /**
     * 创建文件
     */
    public static final File getAbsoluteFile(String uploadDir, String fileName) {
        File desc = new File(uploadDir + File.separator + fileName);

        if (!desc.exists()) {
            if (!desc.getParentFile().exists()) {
                desc.getParentFile().mkdirs();
            }
        }
        return desc;
    }

}
