package com.zszdevelop.file.service.impl;

import cn.hutool.core.io.FileUtil;
import com.zszdevelop.file.config.FileLocalConfig;
import com.zszdevelop.file.domian.FileInfo;
import com.zszdevelop.file.domian.FileResult;
import com.zszdevelop.file.service.LocalFileService;
import com.zszdevelop.file.utils.JfsFileUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

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

        boolean exists = exists(fileName);
        if (!exists) {
            return FileResult.error("下载的文件不存在");
        }
        String fullPath = getFullPath(fileName);
        BufferedInputStream in = FileUtil.getInputStream(fullPath);
        return FileResult.success(in);
    }



    @Override
    public FileResult<FileInfo> upload(MultipartFile file, String filename) {

        FileResult<FileInfo> checkResult = JfsFileUtils.check(file,filename);
        if (!checkResult.isSuccess()) {
            return checkResult;
        }
        try {
            File desc = getAbsoluteFile(filename);
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
        File desc = getAbsoluteFile(filename);
        FileUtil.copy(file,desc,true);

        FileInfo fileInfo = new FileInfo();
        fileInfo.setPath(filename);
        return FileResult.success(fileInfo);
    }

    @Override
    public FileResult<FileInfo> copy(String sourcePath, String targetPath) {
        File src = getAbsoluteFile(sourcePath);
        File desc = getAbsoluteFile(targetPath);
        FileUtil.copy(src,desc,true);

        FileInfo fileInfo = new FileInfo();
        fileInfo.setPath(targetPath);
        return FileResult.success(fileInfo);
    }

    @Override
    public FileResult delete(String fileName) {
        File src = getAbsoluteFile( fileName);
        FileUtil.del(src);
        return FileResult.success("删除成功");
    }

    @Override
    public boolean exists(String fileName) {
        String fullPath = getFullPath(fileName);
        File file = new File(fullPath);
        boolean exists = file.exists();
        return exists;
    }

    @Override
    public String getPreviewUrl(String fileName) {
        return null;
    }


    /**
     * 创建文件
     */
    public  File getAbsoluteFile(String fileName) {
        File desc = new File(getFullPath(fileName));

        if (!desc.exists()) {
            if (!desc.getParentFile().exists()) {
                desc.getParentFile().mkdirs();
            }
        }
        return desc;
    }

    /**
     * 获取全路径
     * @param fileName
     * @return
     */
    private String getFullPath(String fileName) {
        String baseDir = fileLocalConfig.getBaseDir();
        String fullPath = baseDir+File.separator+ fileName;
        return fullPath;
    }

}
