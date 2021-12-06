package com.zszdevelop.file.domian;

import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * @author zsz
 * @date 2021-05-05
 */
@Data
public class FileResult<T> {

    /**
     * 成功码
     */
    public static final int SUCCESS_CODE = 200;
    /**
     * 异常码
     */
    public static final int ERROR_CODE = 500;

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 信息
     */
    private String msg;

    /**
     * 返回结果
     */
    private T data;



    public FileResult(){
        super();
    }

    /**
     * 初始化一个新创建的 FileResult 对象
     *
     * @param code 状态码
     * @param msg 返回内容
     */
    public FileResult(int code, String msg)
    {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 初始化一个新创建的 FileResult 对象
     *
     * @param code 状态码
     * @param msg 返回内容
     * @param data 数据对象
     */
    public FileResult(int code, String msg, T data)
    {
        this(code,msg);
        this.data =  data;
    }


    /**
     * 返回成功消息
     *
     * @return 成功消息
     */
    public static <T> FileResult<T> success()
    {
        return FileResult.success("操作成功");
    }

    /**
     * 返回成功数据
     *
     * @return 成功消息
     */
    public static <T> FileResult<T> success(T data)
    {
        return success("操作成功", data);
    }

    /**
     * 返回成功消息
     *
     * @param msg 返回内容
     * @return 成功消息
     */
    public static <T> FileResult<T> success(String msg)
    {
        return success(msg, null);
    }

    /**
     * 返回成功消息
     *
     * @param msg 返回内容
     * @param data 数据对象
     * @return 成功消息
     */
    public static  <T> FileResult<T> success(String msg, T data)
    {
        return new FileResult<>(SUCCESS_CODE, msg, data);
    }


    /**
     * 返回错误消息
     *
     * @return FileResult
     */
    public static <T> FileResult<T> error()
    {
        return error("操作失败");
    }

    /**
     * 返回错误消息
     *
     * @param msg 返回内容
     * @return 警告消息
     */
    public static <T> FileResult<T> error(String msg)
    {
        return FileResult.error(msg, null);
    }

    /**
     * 返回错误消息
     *
     * @param msg 返回内容
     * @param data 数据对象
     * @return 警告消息
     */
    public static <T> FileResult<T> error(String msg, T data)
    {
        return new FileResult<>(ERROR_CODE, msg, data);
    }


    /**
     * 是否成功
     */
    public boolean isSuccess() {
        return SUCCESS_CODE == code;
    }
}
