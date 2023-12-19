package cn.master.zeus.dto.response;

import lombok.Data;

/**
 * @author Created by 11's papa on 12/19/2023
 **/
@Data
public class ExcelErrData<T> {
    private T t;

    private Integer rowNum;

    private String errMsg;
}
