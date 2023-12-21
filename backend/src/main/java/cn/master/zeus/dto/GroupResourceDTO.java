package cn.master.zeus.dto;

import cn.master.zeus.entity.SystemGroup;
import cn.master.zeus.entity.UserGroupPermission;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author Created by 11's papa on 12/20/2023
 **/
@Data
public class GroupResourceDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private GroupResource resource;
    private List<GroupPermission> permissions;
    private String type;

    private SystemGroup group;
    private List<UserGroupPermission> userGroupPermissions;
}
