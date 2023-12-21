package cn.master.zeus.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * @author Created by 11's papa on 12/20/2023
 **/
@Getter
@Setter
public class ProjectRequest extends BaseRequest {
    private String userId;
    private Map<String, List<String>> filters;
    private Map<String, Object> combine;
}
