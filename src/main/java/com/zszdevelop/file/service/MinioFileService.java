package com.zszdevelop.file.service;

import com.zszdevelop.file.domian.FileResult;
import io.minio.StatObjectResponse;

/**
 * @author zsz
 * @date 2021-05-05
 */
public interface MinioFileService extends FileService {

    /**
     * 获取minio存储的信息
     * @param fileName 文件存储路径(包含文件名和路径)
     * @return
     */
    FileResult<StatObjectResponse> getStatInfo(String fileName);
}
