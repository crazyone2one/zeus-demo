package cn.master.zeus.service;

import cn.master.zeus.dto.GroupDTO;
import cn.master.zeus.dto.GroupPermissionDTO;
import cn.master.zeus.dto.request.group.EditGroupRequest;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import cn.master.zeus.entity.SystemGroup;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 *  服务层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
public interface SystemGroupService extends IService<SystemGroup> {

    List<Map<String, Object>> getAllUserGroup(String userId);

    List<SystemGroup> getGroupByType(EditGroupRequest request);

    Page<GroupDTO> getGroupPageList(EditGroupRequest request);

    boolean deleteGroup(Serializable id);

    SystemGroup addGroup(EditGroupRequest systemGroup);

    GroupPermissionDTO getGroupResource(SystemGroup group);

    void editGroupPermission(EditGroupRequest editGroupRequest);

}
