package cn.master.zeus.common.handler;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author Created by 11's papa on 12/27/2023
 **/
public class ResultHolder {
    public ResultHolder() {
        this.success = true;
    }

    public ResultHolder(Object data) {
        this.data = data;
        this.success = true;
    }

    public ResultHolder(boolean success, String msg) {
        this.success = success;
        this.message = msg;
    }

    public ResultHolder(boolean success, String msg, Object data) {
        this.success = success;
        this.message = msg;
        this.data = data;
    }

    // 请求是否成功
    private boolean success;
    // 描述信息
    private String message;
    // 返回数据
    private Object data = "";

    public boolean isSuccess() {
        return this.success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public static ResultHolder success(Object obj) {
        return new ResultHolder(obj);
    }

    public static ResultHolder error(String message) {
        return new ResultHolder(false, message, null);
    }

    public static ResultHolder error(String message, Object object) {
        return new ResultHolder(false, message, object);
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
