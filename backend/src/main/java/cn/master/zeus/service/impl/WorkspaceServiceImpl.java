package cn.master.zeus.service.impl;

import cn.master.zeus.common.constants.UserGroupConstants;
import cn.master.zeus.common.exception.ServiceException;
import cn.master.zeus.dto.request.BaseRequest;
import cn.master.zeus.dto.request.QueryMemberRequest;
import cn.master.zeus.entity.UserGroup;
import cn.master.zeus.entity.Workspace;
import cn.master.zeus.mapper.UserGroupMapper;
import cn.master.zeus.mapper.WorkspaceMapper;
import cn.master.zeus.service.SystemUserService;
import cn.master.zeus.service.WorkspaceService;
import cn.master.zeus.util.SessionUtils;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static cn.master.zeus.common.exception.enums.GlobalErrorCodeConstants.WORKSPACE_NAME_DUPLICATE;
import static cn.master.zeus.entity.table.WorkspaceTableDef.WORKSPACE;

/**
 * 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class WorkspaceServiceImpl extends ServiceImpl<WorkspaceMapper, Workspace> implements WorkspaceService {
    final UserGroupMapper userGroupMapper;
    final SystemUserService systemUserService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Workspace addWorkspaceByAdmin(Workspace workspace) {
        val userId = SessionUtils.getUserId();
        checkWorkspace(workspace);
        workspace.setCreateUser(userId);
        mapper.insert(workspace);

        val userGroup = UserGroup.builder().userId(userId).sourceId(workspace.getId())
                .groupCode(UserGroupConstants.WS_ADMIN).build();
        userGroupMapper.insert(userGroup);
        return workspace;
    }

    @Override
    public void deleteWorkspace(String workspaceId) {

    }

    @Override
    public void updateWorkspaceByAdmin(Workspace workspace) {

    }

    @Override
    public Page<Workspace> getAllWorkspaceList(BaseRequest request) {
        QueryWrapper wrapper = QueryWrapper.create()
                .where(WORKSPACE.NAME.like(request.getName())).orderBy(WORKSPACE.UPDATE_TIME.desc());
        val paginate = mapper.paginate(Page.of(request.getPageNumber(), request.getPageSize()), wrapper);
        val records = paginate.getRecords();
        if (CollectionUtils.isNotEmpty(records)) {
            records.forEach(item -> {
                QueryMemberRequest queryMemberRequest = new QueryMemberRequest();
                queryMemberRequest.setWorkspaceId(item.getId());
                val memberList = systemUserService.getMemberList(queryMemberRequest);
                item.setMemberSize(memberList.size());
            });
        }
        return paginate;
    }

    private void checkWorkspace(Workspace workspace) {
        val count = QueryChain.of(Workspace.class).where(WORKSPACE.NAME.eq(workspace.getName())
                .and(WORKSPACE.ID.ne(workspace.getId()))).count();
        if (count > 0) {
            throw new ServiceException(WORKSPACE_NAME_DUPLICATE);
        }
    }
}
