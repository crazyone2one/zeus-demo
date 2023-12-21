package cn.master.zeus.dto.request;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author Created by 11's papa on 12/19/2023
 **/
@Data
public class BaseRequest {
    private long pageNumber;
    private long pageSize;
    private String name;
    private String workspaceId;
    private String projectId;

}
