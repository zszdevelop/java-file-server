package com.zszdevelop.file.service;

import com.zszdevelop.file.domian.FileInfo;
import com.zszdevelop.file.domian.FileResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;

/**
 * 文件服务
 *
 * @author zsz
 * @date 2021-29-29
 */
public interface FileService {

    /**
     * 下载文件
     * @param fileName 文件存储路径(包含文件名和路径)
     * @return InputStream
     */
    FileResult<InputStream> download(String fileName);

    /**
     * 上传文件
     * @param multipartFile spring框架下的文件
     * @param filename 文件存储路径(包含文件名和路径)
     * @return
     */
    FileResult<FileInfo> upload(MultipartFile multipartFile, String filename);

    /**
     * 上传文件
     * @param file java普通文件
     * @param filename 文件存储路径(包含文件名和路径)
     * @return
     */
    FileResult<FileInfo> upload(File file, String filename);


    /**
     * 复制文件
     * @param sourcePath 源文件
     * @param targetPath 目标文件
     */
    FileResult<FileInfo> copy(String sourcePath, String targetPath);


    /**
     * 删除文件
     * @param fileName  文件存储路径(包含文件名和路径)
     * @return
     */
    FileResult delete(String fileName);


    /**
     * 文件是否存在
     * @param fileName 文件存储路径(包含文件名和路径)
     * @return
     */
    boolean exists(String fileName);

    /**
     * 获取预览Url
     * @param fileName 文件存储路径(包含文件名和路径)
     * @return
     */
    String getPreviewUrl(String fileName);

}
