package cn.master.zeus.common.exception;

import lombok.Data;

/**
 * 错误码对象
 * @author Created by 11's papa on 12/12/2023
 **/
@Data
public class ErrorCode {
    /**
     * 错误码
     */
    private final Integer code;
    /**
     * 错误提示
     */
    private final String msg;

    public ErrorCode(Integer code, String message) {
        this.code = code;
        this.msg = message;
    }
}
