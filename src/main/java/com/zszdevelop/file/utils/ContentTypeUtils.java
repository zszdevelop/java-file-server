package com.zszdevelop.file.utils;

import cn.hutool.core.io.FileTypeUtil;

import java.io.File;
import java.io.InputStream;

/**
 * 文件预览的content-type。
 * @author zsz
 * @date 2021-05-05
 */
public class ContentTypeUtils {

    public static final String JPG = "jpg";
    public static final String JPEG = "jpeg";
    public static final String PNG = "png";
    public static final   String PDF = "pdf";

    /**
     * 文件类型转
     * @return
     */
    public static String getContentType(File file) {
        String fileType = FileTypeUtil.getType(file);

        fileType = fileType2ContentType(fileType);
        return fileType;
    }


    /**
     * 文件类型转
     * @return
     */
    public static String getContentType(InputStream  is) {
        String fileType = FileTypeUtil.getType(is);

        fileType = fileType2ContentType(fileType);
        return fileType;
    }


    /**
     * 文件类型转contentType
     * @param fileType
     * @return
     */
    private static String fileType2ContentType(String fileType) {
        if (JPG.equals(fileType) || JPEG.equals(fileType)) {
            fileType = "image/jpeg";
        } else if (PNG.equals(fileType)) {
            fileType = "image/png";
        } else if (PDF.equals(fileType)) {
            fileType = "application/pdf";
        }
        return fileType;
    }
}
