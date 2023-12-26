package cn.master.zeus.service.impl;

import cn.master.zeus.common.constants.UserGroupType;
import cn.master.zeus.common.exception.BusinessException;
import cn.master.zeus.convert.user.UserConvert;
import cn.master.zeus.dto.UserDTO;
import cn.master.zeus.dto.UserGroupPermissionDTO;
import cn.master.zeus.dto.request.EditPasswordRequest;
import cn.master.zeus.dto.request.QueryMemberRequest;
import cn.master.zeus.dto.request.UserRequest;
import cn.master.zeus.entity.Project;
import cn.master.zeus.entity.SystemGroup;
import cn.master.zeus.entity.SystemUser;
import cn.master.zeus.entity.UserGroup;
import cn.master.zeus.mapper.SystemUserMapper;
import cn.master.zeus.mapper.UserGroupMapper;
import cn.master.zeus.service.AuthenticationService;
import cn.master.zeus.service.SystemUserService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static cn.master.zeus.common.exception.enums.GlobalErrorCodeConstants.USER_EMAIL_EXISTS;
import static cn.master.zeus.common.exception.enums.GlobalErrorCodeConstants.USER_USERNAME_EXISTS;
import static cn.master.zeus.entity.table.ProjectTableDef.PROJECT;
import static cn.master.zeus.entity.table.SystemGroupTableDef.SYSTEM_GROUP;
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
    final PasswordEncoder passwordEncoder;

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

    @Override
    public Page<SystemUser> getUserPageData(UserRequest request) {
        val queryChain = queryChain().where(SYSTEM_USER.NAME.like(request.getName())
                        .and(SYSTEM_USER.EMAIL.like(request.getEmail())))
                .orderBy(SYSTEM_USER.CREATE_TIME.desc());
        Page<SystemUser> paginate = mapper.paginate(Page.of(request.getPageNumber(), request.getPageSize()), queryChain);
        List<SystemUser> records = paginate.getRecords();
        if (CollectionUtils.isNotEmpty(records)) {
            records.forEach(r -> r.setUserGroupPermissionDTO(getUserGroup(r.getId())));
        }
        return paginate;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDTO insert(cn.master.zeus.dto.request.member.UserRequest systemUser) {
        val nameExist = queryChain().where(SYSTEM_USER.NAME.eq(systemUser.getName())).exists();
        if (nameExist) {
            throw new BusinessException(USER_USERNAME_EXISTS);
        } else {
            val exists = queryChain().where(SYSTEM_USER.EMAIL.eq(systemUser.getEmail())).exists();
            if (exists) {
                throw new BusinessException(USER_EMAIL_EXISTS);
            } else {
                val user = UserConvert.INSTANCE.convert(systemUser);
                user.setStatus(true);
                user.setPassword(passwordEncoder.encode(systemUser.getPassword()));
                mapper.insert(user);
                List<Map<String, Object>> groups = systemUser.getGroups();
                if (!groups.isEmpty()) {
                    insertUserGroup(groups, user.getId());
                }
                return authenticationService.getUserInfo(user.getId());
            }
        }
    }

    @Override
    public UserGroupPermissionDTO getUserGroup(String userId) {
        UserGroupPermissionDTO userGroupPermissionDTO = new UserGroupPermissionDTO();
        List<UserGroup> userGroups = QueryChain.of(UserGroup.class).where(USER_GROUP.USER_ID.eq(userId)).list();
        if (CollectionUtils.isEmpty(userGroups)) {
            return userGroupPermissionDTO;
        }
        userGroupPermissionDTO.setUserGroups(userGroups);
        List<String> groupCode = userGroups.stream().map(UserGroup::getGroupCode).toList();
        List<SystemGroup> groups = QueryChain.of(SystemGroup.class).where(SYSTEM_GROUP.GROUP_CODE.in(groupCode)).list();
        userGroupPermissionDTO.setGroups(groups);
        return userGroupPermissionDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String updateUser(cn.master.zeus.dto.request.member.UserRequest user) {
        QueryChain<UserGroup> userGroupQueryChain = QueryChain.of(UserGroup.class).where(USER_GROUP.USER_ID.eq(user.getId()));
        List<UserGroup> userGroups = userGroupQueryChain.list();
        List<String> list = userGroups.stream().map(UserGroup::getSourceId).toList();
        if (CollectionUtils.isNotEmpty(list)) {
            if (list.contains(user.getLastWorkspaceId())) {
                user.setLastWorkspaceId(null);
                mapper.update(user);
            }
        }
        userGroupMapper.deleteByQuery(userGroupQueryChain);
        List<Map<String, Object>> groups = user.getGroups();
        if (CollectionUtils.isNotEmpty(groups)) {
            insertUserGroup(groups, user.getId());
        }
        long count = queryChain().where(SYSTEM_USER.EMAIL.eq(user.getEmail()).and(SYSTEM_USER.ID.ne(user.getId()))).count();
        if (count > 0) {
            throw new BusinessException(USER_EMAIL_EXISTS);
        }
        mapper.update(user);
        return user.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String updateStatus(SystemUser user) {
        if (StringUtils.isNotBlank(user.getEmail())) {
            long count = queryChain().where(SYSTEM_USER.EMAIL.eq(user.getEmail()).and(SYSTEM_USER.ID.ne(user.getId()))).count();
            if (count > 0) {
                throw new BusinessException(USER_EMAIL_EXISTS);
            }
        }
        user.setPassword(null);
        SystemUser userFromDb = mapper.selectOneById(user.getId());
        if (user.getLastWorkspaceId() != null && !StringUtils.equals(user.getLastWorkspaceId(), userFromDb.getLastWorkspaceId())) {
            List<Project> projects = getProjectListByWsAndUserId(user.getId(), user.getLastWorkspaceId());
            if (CollectionUtils.isNotEmpty(projects)) {
                // 如果传入的 last_project_id 是 last_workspace_id 下面的
                boolean present = projects.stream().anyMatch(p -> StringUtils.equals(p.getId(), user.getLastProjectId()));
                if (!present) {
                    user.setLastProjectId(projects.get(0).getId());
                }
            } else {
                user.setLastProjectId(StringUtils.EMPTY);
            }
        }
        mapper.update(user);
        return user.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateUserPassword(EditPasswordRequest request) {
        SystemUser systemUser = mapper.selectOneById(request.getId());
        String newPassword = request.getNewPassword();
        systemUser.setPassword(passwordEncoder.encode(newPassword));
        return mapper.update(systemUser);
    }

    private void insertUserGroup(List<Map<String, Object>> groups, String userId) {
        groups.forEach(map -> {
            String idType = (String) map.get("type");
            String[] arr = idType.split("\\+");
            String groupId = arr[0];
            String type = arr[1];
            if (StringUtils.equals(type, UserGroupType.SYSTEM)) {
                // 系统用户组
                UserGroup userGroup = new UserGroup();
                userGroup.setUserId(userId);
                userGroup.setGroupCode(groupId);
                userGroup.setSourceId("system");
                userGroupMapper.insert(userGroup);
            } else {
                List<String> ids = (List<String>) map.get("ids");
                //val group = QueryChain.of(SystemGroup.class).where(SYSTEM_GROUP.GROUP_CODE.eq(groupId)).one();
                ids.forEach(id -> {
                    UserGroup userGroup = new UserGroup();
                    userGroup.setUserId(userId);
                    userGroup.setGroupCode(groupId);
                    userGroup.setSourceId(id);
                    userGroupMapper.insert(userGroup);
                });
            }
        });
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
