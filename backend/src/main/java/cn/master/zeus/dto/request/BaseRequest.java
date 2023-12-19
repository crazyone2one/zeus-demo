package cn.master.zeus.dto.request;

import lombok.Data;

/**
 * @author Created by 11's papa on 12/19/2023
 **/
@Data
public class BaseRequest {
    private long pageNumber;
    private long pageSize;
    private String name;
}
