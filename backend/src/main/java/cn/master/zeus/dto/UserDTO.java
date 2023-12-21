package cn.master.zeus.dto;

import cn.master.zeus.entity.SystemGroup;
import cn.master.zeus.entity.SystemUser;
import cn.master.zeus.entity.UserGroup;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by 11's papa on 12/20/2023
 **/
@Setter
@Getter
public class UserDTO extends SystemUser {
    private List<UserGroup> userGroups = new ArrayList<>();
    private List<SystemGroup> groups = new ArrayList<>();
    private List<GroupResourceDTO> groupPermissions = new ArrayList<>();
}
