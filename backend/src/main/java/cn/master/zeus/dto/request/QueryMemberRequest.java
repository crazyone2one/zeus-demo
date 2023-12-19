package cn.master.zeus.dto.request;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Created by 11's papa on 12/19/2023
 **/
@Setter
@Getter
public class QueryMemberRequest {
    private String name;
    private String workspaceId;
    private String projectId;
}
