package com.zszdevelop.file.utils;

import cn.hutool.core.io.FileUtil;
import com.zszdevelop.file.config.FileBaseConfig;
import com.zszdevelop.file.domian.FileResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author zsz
 * @date 2021-11-11
 */
public class JfsFileUtils {


    public static void setResponseHeader(HttpServletResponse response, String realFileName) throws UnsupportedEncodingException
    {
        setResponseHeader(response,realFileName,true);

    }
    /**
     * 下载文件名重新编码
     *
     * @param response 响应对象
     * @param realFileName 真实文件名
     * @return
     */
    public static void setResponseHeader(HttpServletResponse response, String realFileName,boolean isDownload) throws UnsupportedEncodingException
    {
        String percentEncodedFileName = percentEncode(realFileName);

        StringBuilder contentDispositionValue = new StringBuilder();
        contentDispositionValue
                // attachment;附件下载， inline; 在线预览
                .append(isDownload?"attachment":"inline")
                .append("; filename=")
                .append(percentEncodedFileName)
                .append(";")
                .append("filename*=")
                .append("utf-8''")
                .append(percentEncodedFileName);

        response.addHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-disposition", contentDispositionValue.toString());
        response.setHeader("download-filename", percentEncodedFileName);
    }

    /**
     * 百分号编码工具方法
     *
     * @param s 需要百分号编码的字符串
     * @return 百分号编码后的字符串
     */
    public static String percentEncode(String s) throws UnsupportedEncodingException
    {
        String encode = URLEncoder.encode(s, StandardCharsets.UTF_8.toString());
        return encode.replaceAll("\\+", "%20");
    }

    /**
     * 校验文件是否满足需求
     * @return
     */
    public static FileResult check(MultipartFile file,String filename){
        String name = FileUtil.getName(filename);
        int fileNamelength = name.length();
        if (fileNamelength > FileBaseConfig.getFileNameMaxlength())
        {
            return FileResult.error(String.format("超出上传文件名最大长度限制：%s字符",FileBaseConfig.getFileNameMaxlength()));
        }

        long size = file.getSize();
        if (FileBaseConfig.getMaxSize() != -1 && size > FileBaseConfig.getMaxSize())
        {
            return FileResult.error(String.format("超出文件最大大小限制：%sM",FileBaseConfig.getMaxSize() / 1024 / 1024));
        }
        return FileResult.success();
    }

}
