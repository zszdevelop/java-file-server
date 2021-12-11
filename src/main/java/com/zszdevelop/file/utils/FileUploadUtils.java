package com.zszdevelop.file.utils;

import cn.hutool.core.io.FileUtil;
import com.zszdevelop.file.config.FileBaseConfig;
import com.zszdevelop.file.domian.FileResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author zsz
 * @date 2021-11-11
 */
public class FileUploadUtils {

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
