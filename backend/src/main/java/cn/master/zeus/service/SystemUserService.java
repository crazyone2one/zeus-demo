package cn.master.zeus.service;

import cn.master.zeus.dto.UserDTO;
import cn.master.zeus.dto.UserGroupPermissionDTO;
import cn.master.zeus.dto.request.EditPasswordRequest;
import cn.master.zeus.dto.request.QueryMemberRequest;
import cn.master.zeus.dto.request.UserRequest;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import cn.master.zeus.entity.SystemUser;

import java.util.List;

/**
 *  服务层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
public interface SystemUserService extends IService<SystemUser> {
    List<SystemUser> getMemberList(QueryMemberRequest request);

    UserDTO switchUserResource(String workspace, String sourceId, String username);

    List<SystemUser> getProjectMemberList(QueryMemberRequest request);

    Page<SystemUser> getUserPageData(UserRequest request);

    UserDTO insert(cn.master.zeus.dto.request.member.UserRequest systemUser);

    UserGroupPermissionDTO getUserGroup(String userId);

    String updateUser(cn.master.zeus.dto.request.member.UserRequest systemUser);

    String updateStatus(SystemUser systemUser);

    int updateUserPassword(EditPasswordRequest request);
}
