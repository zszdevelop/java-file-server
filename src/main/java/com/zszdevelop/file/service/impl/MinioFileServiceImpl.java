package com.zszdevelop.file.service.impl;

import com.zszdevelop.file.config.MinioProperties;
import com.zszdevelop.file.domian.FileInfo;
import com.zszdevelop.file.domian.FileResult;
import com.zszdevelop.file.service.MinioFileService;
import com.zszdevelop.file.utils.ContentTypeUtils;
import com.zszdevelop.file.utils.JfsFileUtils;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

/**
 * minio实现文件服务
 *
 * @author zsz
 * @date 2021-29-29
 */
@Slf4j
@Primary
@Service
public class MinioFileServiceImpl implements MinioFileService {

    @Resource
    private MinioClient minioClient;


    @Resource
    private MinioProperties minioProperties;


    /**
     * 下载文件
     *
     * @param fileName 文件存储路径(包含文件名和路径)
     * @return InputStream
     */
    @Override
    public FileResult<InputStream> download(String fileName) {
        boolean exists = exists(fileName);
        if (!exists) {
            return FileResult.error("下载的文件不存在");
        }
        try {
            GetObjectArgs objectArgs = GetObjectArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .object(fileName)
//                    .offset(1024L)
//                    .length(4096L)
                    .build();
            InputStream in = minioClient.getObject(objectArgs);
            return FileResult.success(in);
        } catch (Exception e) {
            log.error("FileService  minio文件下载失败{}", e.getMessage(), e);
            return FileResult.error("minio文件下载失败");
        }
    }

    /**
     * 上传文件
     *
     * @param multipartFile 多文件上传
     * @param filename      文件存储路径(包含文件名和路径)
     * @return
     */
    @Override
    public FileResult<FileInfo> upload(MultipartFile multipartFile, String filename) {

        FileResult<FileInfo> checkResult = JfsFileUtils.check(multipartFile,filename);
        if (!checkResult.isSuccess()) {
            return checkResult;
        }
        try {
            InputStream in = multipartFile.getInputStream();
            String contentType = multipartFile.getContentType();
            if (StringUtils.isEmpty(filename)) {
                filename = multipartFile.getOriginalFilename();
            }
            long fileLength = multipartFile.getSize();

            // 具体做上传文件操作
            FileInfo info = doUpload(in, contentType, filename, fileLength);
            return FileResult.success(info);
        } catch (Exception e) {
            log.error("FileService  minio文件上传失败{}", e.getMessage(), e);
            return FileResult.error("minio文件上传失败");
        }

    }


    /**
     * 上传文件
     *
     * @param file     java普通文件
     * @param filename 文件存储路径(包含文件名和路径)
     * @return
     */
    @Override
    public FileResult<FileInfo> upload(File file, String filename) {

        try {
            //文件上传
            InputStream in = new FileInputStream(file);
            String contentType = ContentTypeUtils.getContentType(file);
            if (StringUtils.isEmpty(filename)) {
                filename = file.getName();
            }
            long fileLength = file.length();

            // 具体做上传文件操作
            FileInfo info = doUpload(in, contentType, filename, fileLength);
            return FileResult.success(info);
        } catch (Exception e) {
            log.error("FileService  minio文件上传失败{}", e.getMessage(), e);
            return FileResult.error("minio文件上传失败");
        }
    }


    /**
     * 复制文件
     *
     * @param sourcePath 源文件
     * @param targetPath 目标文件
     */
    @Override
    public FileResult<FileInfo> copy(String sourcePath, String targetPath) {
        try {
            String bucketName = minioProperties.getBucketName();
            CopyObjectArgs args = CopyObjectArgs.builder()
                    .bucket(bucketName)
                    .object(targetPath)
                    .source(CopySource.builder()
                            .bucket(bucketName)
                            .object(sourcePath)
                            .build())
                    .build();
            ObjectWriteResponse response = minioClient.copyObject(args);
            FileInfo info = new FileInfo();
            info.setPath(response.object());
            info.put("versionId", response.versionId());
            info.put("etag", response.etag());
            return FileResult.success(info);
        } catch (Exception e) {
            log.error("FileService  minio文件服务失败{}", e.getMessage(), e);
            return FileResult.error("minio文件上传失败");
        }
    }


    /**
     * 文件是否存在
     *
     * @param fileName
     * @return
     */
    @Override
    public boolean exists(String fileName) {

        FileResult<StatObjectResponse> fileResult = getStatInfo(fileName);
        return fileResult.isSuccess();
    }


    /**
     * 删除文件
     * @param fileName  文件存储路径(包含文件名和路径)
     * @return
     */
    @Override
    public FileResult delete(String fileName) {
        try {
            RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder().bucket(minioProperties.getBucketName()).object(fileName).build();
            minioClient.removeObject(removeObjectArgs);
            return FileResult.success("删除成功");
        } catch (Exception e) {
            log.error("FileService  minio文件服务删除失败{}", e.getMessage(), e);
            return FileResult.error("minio文件服务删除失败");
        }
    }


    @Override
    public String getPreviewUrl(String fileName) {
        Integer expiryTime = minioProperties.getExpiry();
        String url = null;
        try {
            GetPresignedObjectUrlArgs.Builder builder = GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(minioProperties.getBucketName())
                    .object(fileName);
            // 过期时间设置为0的时候，不设置过期时间
            if (expiryTime!=null&&expiryTime!=0){
                builder .expiry(expiryTime, TimeUnit.MINUTES);
            }
            GetPresignedObjectUrlArgs objectUrlArgs = builder.build();
            url = minioClient.getPresignedObjectUrl(objectUrlArgs);
        } catch (Exception e) {
            log.error("FileService  minio文件获取预览Url失败{}", e.getMessage(), e);
        }

        return url;
    }


    /**
     * 做具体上传操作
     */
    private FileInfo doUpload(InputStream in, String contentType, String filename, long fileLength) throws IOException, ErrorResponseException, InsufficientDataException, InternalException, InvalidKeyException, InvalidResponseException, NoSuchAlgorithmException, ServerException, XmlParserException {
        //文件上传

        PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                .bucket(minioProperties.getBucketName())
                .object(filename)
                .stream(in, fileLength, -1)
                .contentType(contentType)
                .build();
        ObjectWriteResponse response = minioClient.putObject(putObjectArgs);

        in.close();

        FileInfo info = new FileInfo();
        info.setPath(response.object());
        info.put("versionId", response.versionId());
        info.put("etag", response.etag());
        return info;
    }

    /**
     * 获取minio存储的信息
     * @param fileName
     * @return
     */
    @Override
    public FileResult<StatObjectResponse> getStatInfo(String fileName) {
        // 获取对象信息
        try {
            // 调用statObject()来判断对象是否存在。
            // 如果不存在, statObject()抛出异常,
            // 否则则代表对象存在。
            StatObjectArgs statObjectArgs = StatObjectArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .object(fileName)
                    .build();
            StatObjectResponse stat = minioClient.statObject(statObjectArgs);
            return FileResult.success(stat);
        } catch (Exception e) {
            log.info("校验文件是否存在： file: {} 不存在, e: ", fileName, e);
            return FileResult.error("文件不存在");
        }

    }



//    @Override
//    public List<Object> list() throws Exception {
//        //获取bucket列表
//        Iterable<Result<Item>> myObjects = minioClient.listObjects(
//                ListObjectsArgs.builder().bucket(bucketName).build());
//        Iterator<Result<Item>> iterator = myObjects.iterator();
//        List<Object> items = new ArrayList<>();
//        String format = "{'fileName':'%s','fileSize':'%s'}";
//        while (iterator.hasNext()) {
//            Item item = iterator.next().get();
//            items.add(JSON.parse(String.format(format, item.objectName(),
//                    formatFileSize(item.size()))));
//        }
//        return items;
//    }

}
