package cn.master.zeus.service.impl;

import cn.master.zeus.common.constants.SystemReference;
import cn.master.zeus.common.constants.UserGroupConstants;
import cn.master.zeus.common.exception.ServiceException;
import cn.master.zeus.dto.RelatedSource;
import cn.master.zeus.dto.request.BaseRequest;
import cn.master.zeus.dto.request.QueryMemberRequest;
import cn.master.zeus.dto.response.DetailColumn;
import cn.master.zeus.dto.response.OperatingLogDetails;
import cn.master.zeus.entity.UserGroup;
import cn.master.zeus.entity.Workspace;
import cn.master.zeus.mapper.UserGroupMapper;
import cn.master.zeus.mapper.WorkspaceMapper;
import cn.master.zeus.service.SystemUserService;
import cn.master.zeus.service.WorkspaceService;
import cn.master.zeus.util.JsonUtils;
import cn.master.zeus.util.ReflexObjectUtil;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static cn.master.zeus.common.exception.enums.GlobalErrorCodeConstants.WORKSPACE_NAME_DUPLICATE;
import static cn.master.zeus.entity.table.ProjectTableDef.PROJECT;
import static cn.master.zeus.entity.table.UserGroupTableDef.USER_GROUP;
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
        paginate.setRecords(records);
        return paginate;
    }

    @Override
    public String getLogDetails(String workspaceId) {
        val workspace = mapper.selectOneById(workspaceId);
        List<DetailColumn> columns = ReflexObjectUtil.getColumns(workspace, SystemReference.organizationColumns);
        OperatingLogDetails details = new OperatingLogDetails(workspace.getId(), null, workspace.getName(), workspace.getCreateUser(), columns);
        return JsonUtils.toJsonString(details);
    }

    @Override
    public List<Workspace> getWorkspaceListByUserId(String userId) {
        val superUser = userGroupMapper.isSuperUser(userId);
        if (superUser) {
            return mapper.selectAll();
        }
        List<RelatedSource> relatedSource = getRelatedSource(userId);
        assert relatedSource != null;
        List<String> wsIds = relatedSource
                .stream()
                .map(RelatedSource::getWorkspaceId)
                .distinct()
                .toList();
        if (org.springframework.util.CollectionUtils.isEmpty(wsIds)) {
            return new ArrayList<>();
        }
        return queryChain().where(WORKSPACE.ID.in(wsIds)).list();
    }

    private List<RelatedSource> getRelatedSource(String userId) {
        val queryChain = QueryChain.of(UserGroup.class).select(PROJECT.WORKSPACE_ID.as("workspaceId"), PROJECT.ID.as("projectId"))
                .from(USER_GROUP)
                .join(PROJECT).on(USER_GROUP.SOURCE_ID.eq(PROJECT.ID))
                .join(WORKSPACE).on(PROJECT.WORKSPACE_ID.eq(WORKSPACE.ID))
                .where(USER_GROUP.USER_ID.eq(userId)).union(
                        QueryChain.of(UserGroup.class).select(WORKSPACE.ID.as("workspaceId"))
                                .select("''")
                                .from(USER_GROUP)
                                .join(WORKSPACE).on(USER_GROUP.SOURCE_ID.eq(WORKSPACE.ID))
                                .where(USER_GROUP.USER_ID.eq(userId))
                );
        return mapper.selectListByQueryAs(queryChain, RelatedSource.class);
    }

    private void checkWorkspace(Workspace workspace) {
        val count = QueryChain.of(Workspace.class).where(WORKSPACE.NAME.eq(workspace.getName())
                .and(WORKSPACE.ID.ne(workspace.getId()))).count();
        if (count > 0) {
            throw new ServiceException(WORKSPACE_NAME_DUPLICATE);
        }
    }
}
