package com.spring.security.filter.enums;

import com.spring.security.response.ResponseResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author qixlin
 * @date 2021/03/31 11:25
 */
public enum ApiStatus {

    /**
     * 参数校验失败
     */
    INVALIDATE(40001, "参数校验失败"),
    /**
     * 签名校验失败
     */
    VALIDATE_FAILED(40002, "签名校验失败"),
    /**
     * 未记录的appId
     */
    NO_RECORD_APP_ID(40400, "无效的appId"),
    /**
     * 权限不足
     */
    NO_PERMISSION(40403, "权限不足"),

    NOT_SUPPORTED(40405, "不支持的请求方式"),

    ABNORMAL_REQUEST(40500, "请求异常"),

    /**
     * 时间偏移过大
     */
    TIME_OFFSET_TOO_BIG(40402, "与服务器时间相差过大，前后不能超过1分钟");

    private final Integer code;

    private final String msg;

    ApiStatus(int i, String msg) {
        this.code = i;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public ResponseResult ofResult() {
        String msg =  this.msg;
        if (this == TIME_OFFSET_TOO_BIG) {
            msg += ",当前时间："+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        return new ResponseResult(this.code, msg);
    }
}
