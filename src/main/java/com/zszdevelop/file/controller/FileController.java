package com.zszdevelop.file.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import com.zszdevelop.file.domian.FileInfo;
import com.zszdevelop.file.domian.FileResult;
import com.zszdevelop.file.service.FileService;
import com.zszdevelop.file.utils.ContentTypeUtils;
import com.zszdevelop.file.utils.FileUploadUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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
            response.setHeader("Content-Disposition", "attachment;filename=" +
                    URLEncoder.encode(fileName, "UTF-8"));
            FileResult<InputStream> fileResult = fileService.download(fileName);
            if (fileResult.isSuccess()) {
                InputStream in = fileResult.getData();
                response.setContentType("application/x-msdownload");
                response.setHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes(), "ISO-8859-1"));
                response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
                ServletOutputStream out = response.getOutputStream();
                IoUtil.copy(in, out);
                IoUtil.close(out);
                IoUtil.close(in);
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
            ServletOutputStream out = response.getOutputStream();
            FileResult<InputStream> fileResult = fileService.download(fileName);
            InputStream in = fileResult.getData();
            // response.setContentType() 不用设置，会以最合适的方式下载
            // attachment;附件下载， inline; 在线预览
            response.setHeader("Content-Disposition", "inline; filename=" + URLEncoder.encode(fileName, "UTF-8"));
            IoUtil.copy(in, out);
            IoUtil.close(out);
            IoUtil.close(in);
        } catch (IOException e) {
            e.printStackTrace();
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
        return FileResult.success("成功", exists);
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