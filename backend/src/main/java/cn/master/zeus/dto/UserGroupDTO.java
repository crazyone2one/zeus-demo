package cn.master.zeus.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Created by 11's papa on 12/25/2023
 **/
@Setter
@Getter
public class UserGroupDTO {
    private String userId;
    @JsonProperty("groupId")
    private String groupCode;
    private String sourceId;
    private String name;
    /**
     * 用户组所属类型
     */
    private String type;
}
