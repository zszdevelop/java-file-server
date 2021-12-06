package com.zszdevelop.file.domian;

import lombok.Data;

import java.util.HashMap;

/**
 * 文件上传信息
 * @author zsz
 * @date 2021-05-05
 */
public class FileInfo extends HashMap<String, Object> {


    /** 文件服务存储地址 */
    public static final String PATH_TAG = "path";


    public void setPath(String path)
    {
        super.put(PATH_TAG, path);
    }


    public String getPath() {
        return (String) get(PATH_TAG);
    }

    /**
     * 方便链式调用
     *
     * @param key 键
     * @param value 值
     * @return 数据对象
     */
    @Override
    public FileInfo put(String key, Object value)
    {
        super.put(key, value);
        return this;
    }
}
