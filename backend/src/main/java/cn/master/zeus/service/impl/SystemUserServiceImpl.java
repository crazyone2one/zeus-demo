package cn.master.zeus.service.impl;

import cn.master.zeus.dto.UserDTO;
import cn.master.zeus.dto.request.QueryMemberRequest;
import cn.master.zeus.entity.Project;
import cn.master.zeus.entity.UserGroup;
import cn.master.zeus.mapper.UserGroupMapper;
import cn.master.zeus.service.AuthenticationService;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.zeus.entity.SystemUser;
import cn.master.zeus.mapper.SystemUserMapper;
import cn.master.zeus.service.SystemUserService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static cn.master.zeus.entity.table.ProjectTableDef.PROJECT;
import static cn.master.zeus.entity.table.SystemUserTableDef.SYSTEM_USER;
import static cn.master.zeus.entity.table.UserGroupTableDef.USER_GROUP;

/**
 * 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class SystemUserServiceImpl extends ServiceImpl<SystemUserMapper, SystemUser> implements SystemUserService {
    final AuthenticationService authenticationService;
    final UserGroupMapper userGroupMapper;

    @Override
    public List<SystemUser> getMemberList(QueryMemberRequest request) {
        QueryWrapper wrapper = new QueryWrapper();
        QueryWrapper query = new QueryWrapper()
                .select("distinct *").from(
                        wrapper.select(SYSTEM_USER.ALL_COLUMNS)
                                .from(USER_GROUP).join(SYSTEM_USER).on(USER_GROUP.USER_ID.eq(SYSTEM_USER.ID))
                                .where(USER_GROUP.SOURCE_ID.eq(request.getWorkspaceId()))
                                .and(SYSTEM_USER.NAME.like(request.getName(), StringUtils.isNoneBlank(request.getName())))
                                .orderBy(USER_GROUP.UPDATE_TIME.desc())
                ).as("temp");
        return mapper.selectListByQuery(query);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDTO switchUserResource(String sign, String sourceId, String username) {
        val user = queryChain().where(SYSTEM_USER.NAME.eq(username)).one();
        val superUser = userGroupMapper.isSuperUser(user.getId());
        if (StringUtils.equals("workspace", sign)) {
            user.setLastWorkspaceId(sourceId);
            user.setLastProjectId(StringUtils.EMPTY);
            List<Project> projects = getProjectListByWsAndUserId(user.getId(), sourceId);
            if (CollectionUtils.isNotEmpty(projects)) {
                user.setLastProjectId(projects.get(0).getId());
            } else {
                if (superUser) {
                    val projectList = QueryChain.of(Project.class).where(PROJECT.WORKSPACE_ID.eq(sourceId)).list();
                    if (CollectionUtils.isNotEmpty(projectList)) {
                        user.setLastProjectId(projectList.get(0).getId());
                    }
                } else {
                    user.setLastProjectId(StringUtils.EMPTY);
                }
            }
        }
        mapper.update(user);
        return authenticationService.getUserInfo(user.getId());
    }

    @Override
    public List<SystemUser> getProjectMemberList(QueryMemberRequest request) {
        QueryWrapper wrapper = new QueryWrapper();
        QueryWrapper query = new QueryWrapper()
                .select("distinct *").from(
                        wrapper.select(SYSTEM_USER.ALL_COLUMNS)
                                .from(USER_GROUP).join(SYSTEM_USER).on(USER_GROUP.USER_ID.eq(SYSTEM_USER.ID))
                                .where(USER_GROUP.SOURCE_ID.eq(request.getProjectId()))
                                .orderBy(SYSTEM_USER.UPDATE_TIME.desc())
                ).as("temp");
        return mapper.selectListByQuery(query);
    }

    private List<Project> getProjectListByWsAndUserId(String userId, String sourceId) {
        val projects = QueryChain.of(Project.class).where(PROJECT.WORKSPACE_ID.eq(sourceId)).list();
        val userGroups = QueryChain.of(UserGroup.class).where(USER_GROUP.USER_ID.eq(userId)).list();
        List<Project> projectList = new ArrayList<>();
        userGroups.forEach(userGroup -> projects.forEach(project -> {
            if (StringUtils.equals(userGroup.getSourceId(), project.getId())) {
                if ((!projectList.contains(project))) {
                    projectList.add(project);
                }
            }
        }));
        return projectList;
    }
}
