package cn.master.zeus.service.impl;

import cn.master.zeus.common.constants.UserGroupConstants;
import cn.master.zeus.common.exception.BusinessException;
import cn.master.zeus.dto.request.ProjectRequest;
import cn.master.zeus.dto.request.QueryMemberRequest;
import cn.master.zeus.entity.Project;
import cn.master.zeus.entity.ProjectVersion;
import cn.master.zeus.entity.UserGroup;
import cn.master.zeus.mapper.ProjectMapper;
import cn.master.zeus.mapper.ProjectVersionMapper;
import cn.master.zeus.mapper.UserGroupMapper;
import cn.master.zeus.service.ProjectService;
import cn.master.zeus.service.SystemUserService;
import cn.master.zeus.util.SessionUtils;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import static cn.master.zeus.entity.table.ProjectTableDef.PROJECT;
import static cn.master.zeus.entity.table.SystemGroupTableDef.SYSTEM_GROUP;
import static cn.master.zeus.entity.table.SystemUserTableDef.SYSTEM_USER;
import static cn.master.zeus.entity.table.UserGroupTableDef.USER_GROUP;
import static cn.master.zeus.entity.table.WorkspaceTableDef.WORKSPACE;
import static com.mybatisflex.core.query.QueryMethods.distinct;

/**
 * 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements ProjectService {
    final UserGroupMapper userGroupMapper;
    final ProjectVersionMapper projectVersionMapper;
    final SystemUserService systemUserService;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public Project addProject(Project project) {
        saveProject(project);
        // 创建项目为当前用户添加用户组
        val userGroup = UserGroup.builder()
                .userId(SessionUtils.getUserId())
                .groupCode(UserGroupConstants.PROJECT_ADMIN)
                .sourceId(project.getId())
                .build();
        userGroupMapper.insert(userGroup);
        // 创建默认版本
        addProjectVersion(project);
        return project;
    }

    @Override
    public Page<Project> getProjectPageList(ProjectRequest request) {
        val queryChain = queryChain().select(PROJECT.ID, PROJECT.WORKSPACE_ID, PROJECT.NAME, PROJECT.DESCRIPTION, PROJECT.ISSUE_TEMPLATE_ID, PROJECT.CASE_TEMPLATE_ID,
                        WORKSPACE.ID.as("workspaceId"), WORKSPACE.NAME.as("workspaceName"),
                        SYSTEM_USER.NAME.as("createUserName")).from(PROJECT)
                .join(WORKSPACE).on(PROJECT.WORKSPACE_ID.eq(WORKSPACE.ID))
                .join(SYSTEM_USER).on(PROJECT.CREATE_USER.eq(SYSTEM_USER.ID))
                .where(PROJECT.NAME.like(request.getName())
                        .and(WORKSPACE.ID.eq(request.getWorkspaceId()))
                        .and(PROJECT.ID.eq(request.getProjectId())));
        val page = mapper.paginateAs(Page.of(request.getPageNumber(), request.getPageSize()), queryChain, Project.class);
        val records = page.getRecords();
        if (CollectionUtils.isNotEmpty(records)) {
            records.forEach(r -> {
                QueryMemberRequest request1 = new QueryMemberRequest();
                request1.setProjectId(r.getId());
                r.setMemberSize(systemUserService.getMemberList(request1).size());
            });
        }
        return page;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteProject(Serializable id) {
        // User Group
        userGroupMapper.deleteByQuery(QueryChain.of(UserGroup.class).where(USER_GROUP.SOURCE_ID.eq(id)));
        // delete project
        return mapper.deleteById(id);
    }

    @Override
    public long getProjectMemberSize(String id) {
        return QueryChain.of(UserGroup.class).select(distinct(SYSTEM_USER.ID))
                .from(USER_GROUP).join(SYSTEM_USER).on(SYSTEM_USER.ID.eq(USER_GROUP.USER_ID))
                .where(USER_GROUP.SOURCE_ID.eq(id)).count();
    }

    @Override
    public List<Project> getUserProject(ProjectRequest request) {
        boolean superUser = userGroupMapper.isSuperUser(SessionUtils.getUserId());
        if (superUser) {
            return queryChain().where(PROJECT.NAME.like(request.getName())
                    .and(PROJECT.WORKSPACE_ID.eq(request.getWorkspaceId()))).list();
        }
        return queryChain().select(distinct(PROJECT.ALL_COLUMNS))
                .from(SYSTEM_GROUP)
                .join(USER_GROUP).on(USER_GROUP.GROUP_CODE.eq(SYSTEM_GROUP.GROUP_CODE))
                .join(PROJECT).on(PROJECT.ID.eq(USER_GROUP.SOURCE_ID))
                .where(SYSTEM_GROUP.TYPE.eq("PROJECT").and(USER_GROUP.USER_ID.eq(request.getUserId()))
                        .and(PROJECT.WORKSPACE_ID.eq(request.getWorkspaceId()))
                        .and(PROJECT.NAME.like(request.getName()))).list();
    }

    private void addProjectVersion(Project project) {
        val build = ProjectVersion.builder()
                .name("v1.0.0")
                .projectId(project.getId())
                .startTime(LocalDateTime.now())
                .publishTime(LocalDateTime.now())
                .status("open")
                .latest(true)
                .build();
        projectVersionMapper.insert(build);
    }

    private void saveProject(Project project) {
        val exists = queryChain().where(PROJECT.WORKSPACE_ID.eq(project.getWorkspaceId()).and(PROJECT.NAME.eq(project.getName()))).exists();
        if (exists) {
            BusinessException.throwException("项目名称已存在");
        }
        project.setSystemId(genSystemId());
        project.setCreateUser(SessionUtils.getUserId());
        mapper.insert(project);
    }

    private String genSystemId() {
        val maxSystemIdInDb = mapper.getMaxSystemId();
        String systemId = "10001";
        if (StringUtils.isNotEmpty(maxSystemIdInDb)) {
            systemId = String.valueOf(Long.parseLong(maxSystemIdInDb) + 1);
        }
        return systemId;
    }
}
