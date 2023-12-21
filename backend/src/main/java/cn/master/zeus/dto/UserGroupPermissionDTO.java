package cn.master.zeus.dto;

import cn.master.zeus.entity.SystemGroup;
import cn.master.zeus.entity.UserGroup;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by 11's papa on 12/20/2023
 **/
@Data
public class UserGroupPermissionDTO {
    List<GroupResourceDTO> list = new ArrayList<>();
    List<SystemGroup> groups = new ArrayList<>();
    List<UserGroup> userGroups = new ArrayList<>();
}
