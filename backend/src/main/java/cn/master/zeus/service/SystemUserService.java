package cn.master.zeus.service;

import cn.master.zeus.dto.UserDTO;
import cn.master.zeus.dto.request.QueryMemberRequest;
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
}
