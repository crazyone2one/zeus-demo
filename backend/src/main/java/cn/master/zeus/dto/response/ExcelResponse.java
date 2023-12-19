package cn.master.zeus.dto.response;

import lombok.Data;

import java.util.List;

/**
 * @author Created by 11's papa on 12/19/2023
 **/
@Data
public class ExcelResponse <T>{
    private Boolean success;
    private List<ExcelErrData<T>> errList;
    private Boolean isUpdated;  //是否有更新过用例
}
