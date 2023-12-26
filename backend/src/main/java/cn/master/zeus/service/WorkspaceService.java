package cn.master.zeus.service;

import cn.master.zeus.dto.WorkspaceResource;
import cn.master.zeus.dto.request.BaseRequest;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import cn.master.zeus.entity.Workspace;

import java.util.List;

/**
 *  服务层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
public interface WorkspaceService extends IService<Workspace> {

    Workspace addWorkspaceByAdmin(Workspace workspace);

    void deleteWorkspace(String workspaceId);

    void updateWorkspaceByAdmin(Workspace workspace);

    Page<Workspace> getAllWorkspaceList(BaseRequest request);

    String getLogDetails(String workspaceId);

    List<Workspace> getWorkspaceListByUserId(String userId);

    WorkspaceResource listResource(String groupCode, String type);

    List<Workspace> getWorkspaceList(BaseRequest baseRequest);
}
