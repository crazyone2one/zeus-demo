package cn.master.zeus.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Created by 11's papa on 12/12/2023
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ServiceException extends RuntimeException {
    private Integer code;
    private String message;

    public ServiceException(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMsg();
    }

    public ServiceException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
