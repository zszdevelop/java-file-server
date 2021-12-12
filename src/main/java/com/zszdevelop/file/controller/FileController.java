package com.zszdevelop.file.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import cn.hutool.json.serialize.JSONWriter;
import com.zszdevelop.file.domian.FileInfo;
import com.zszdevelop.file.domian.FileResult;
import com.zszdevelop.file.service.FileService;
import com.zszdevelop.file.utils.JfsFileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 文件服务控制器
 *
 * @author zsz
 * @date 2021-29-29
 */
@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {

    @Resource
    FileService fileService;

    /**
     * 上传文件
     *
     * @param multipartFiles 上传文件列表
     * @return
     */
    @PostMapping("/upload")
    public FileResult<List<FileResult<FileInfo>>> upload(@RequestParam(name = "file", required = false)
                                                                 MultipartFile[] multipartFiles) {
        if (multipartFiles == null || multipartFiles.length == 0) {
            return FileResult.error("上传文件不能为空");
        }
        List<FileResult<FileInfo>> list = new ArrayList<>();
        for (MultipartFile file : multipartFiles) {
            String pathFilename = getNewPathFilename(file);
            FileResult<FileInfo> fileResult = fileService.upload(file, pathFilename);
            list.add(fileResult);
        }

        return FileResult.success("上传成功", list);
    }




    /**
     * 下载文件
     *
     * @param fileName 文件存储路径(包含文件名和路径)
     */
    @GetMapping("/download")
    public void download(HttpServletResponse response, String fileName) {

        try {
            FileResult<InputStream> fileResult = fileService.download(fileName);
            if (fileResult.isSuccess()) {
                InputStream in = fileResult.getData();

                String downloadName = fileName.substring(fileName.lastIndexOf("/")+1);;
                response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
                JfsFileUtils.setResponseHeader(response, downloadName);

                ServletOutputStream out = response.getOutputStream();
                IoUtil.copy(in, out);
                IoUtil.close(out);
                IoUtil.close(in);
            }else {
                response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
                PrintWriter writer=response.getWriter();
                writer.write(JSONUtil.toJsonStr(fileResult));
                writer.flush();
            }

        } catch (Exception e) {
            log.error("FileController  文件下载失败{}", e.getMessage(), e);
        }

    }

    /**
     * 预览文件
     *
     * @param fileName 文件存储路径(包含文件名和路径)
     */
    @GetMapping("/preview")
    public void preview(HttpServletResponse response, String fileName) {
        try {

            FileResult<InputStream> fileResult = fileService.download(fileName);
            if (fileResult.isSuccess()) {
                ServletOutputStream out = response.getOutputStream();
                InputStream in = fileResult.getData();
                // response.setContentType() 不用设置，会以最合适的方式下载
                String downloadName = fileName.substring(fileName.lastIndexOf("/") + 1);

                JfsFileUtils.setResponseHeader(response, downloadName, false);
                IoUtil.copy(in, out);
                IoUtil.close(out);
                IoUtil.close(in);
            }else {
                response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
                PrintWriter writer=response.getWriter();
                writer.write(JSONUtil.toJsonStr(fileResult));
                writer.flush();
            }
        } catch (IOException e) {
            log.error("FileService  文件预览失败{}", e.getMessage(), e);
        }

    }

    /**
     * 获取文件预览地址
     *
     * @param fileName 文件存储路径(包含文件名和路径)
     */
    @GetMapping("/getPreviewUrl")
    public FileResult<String> getPreviewUrl(String fileName) {
        String signedUrl = fileService.getPreviewUrl(fileName);
        return FileResult.success("成功", signedUrl);
    }

    /**
     * 文件是否存在
     *
     * @param fileName 文件存储路径(包含文件名和路径)
     */
    @GetMapping("/exists")
    public FileResult<Boolean> exists(String fileName) {
        boolean exists = fileService.exists(fileName);
        FileResult<Boolean> result = FileResult.success("成功", exists);
        return result;
    }


    /**
     * 删除文件
     *
     * @param fileName 文件存储路径(包含文件名和路径)
     * @return
     */
    @DeleteMapping("/delete")
    public FileResult delete(String fileName) {
        FileResult fileResult = fileService.delete(fileName);
        return fileResult;
    }


    /**
     * 获取包含路径的文件名
     * TODO 此处根据项目实际情况处理，制定存储路径和重命名文件
     * @param file
     * @return
     */
    private String getNewPathFilename(MultipartFile file) {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String datePath =  sdf.format(now);
        String extension = FileUtil.extName(file.getOriginalFilename());

        String pathFilename = datePath + File.separator + IdUtil.fastSimpleUUID() + "." + extension;
        return pathFilename;
    }

}