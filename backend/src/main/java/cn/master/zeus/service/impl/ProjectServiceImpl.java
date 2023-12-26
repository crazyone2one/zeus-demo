package cn.master.zeus.service.impl;

import cn.master.zeus.common.constants.UserGroupConstants;
import cn.master.zeus.common.exception.BusinessException;
import cn.master.zeus.dto.request.ProjectRequest;
import cn.master.zeus.dto.request.QueryMemberRequest;
import cn.master.zeus.entity.ProjectVersion;
import cn.master.zeus.entity.UserGroup;
import cn.master.zeus.mapper.ProjectVersionMapper;
import cn.master.zeus.mapper.UserGroupMapper;
import cn.master.zeus.service.SystemUserService;
import cn.master.zeus.util.SessionUtils;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.zeus.entity.Project;
import cn.master.zeus.mapper.ProjectMapper;
import cn.master.zeus.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static cn.master.zeus.common.exception.enums.GlobalErrorCodeConstants.PROJECT_NAME_DUPLICATE;
import static cn.master.zeus.entity.table.ProjectTableDef.PROJECT;
import static cn.master.zeus.entity.table.SystemUserTableDef.SYSTEM_USER;
import static cn.master.zeus.entity.table.WorkspaceTableDef.WORKSPACE;

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
            records.forEach(r->{
                QueryMemberRequest request1 = new QueryMemberRequest();
                request1.setProjectId(r.getId());
                r.setMemberSize(systemUserService.getMemberList(request1).size());
            });
        }
        return page;
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
            throw new BusinessException(PROJECT_NAME_DUPLICATE);
        }
        project.setSystemId(genSystemId());
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
