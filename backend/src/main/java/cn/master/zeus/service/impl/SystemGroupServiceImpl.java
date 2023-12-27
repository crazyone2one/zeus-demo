package cn.master.zeus.service.impl;

import cn.master.zeus.common.constants.UserGroupConstants;
import cn.master.zeus.common.constants.UserGroupType;
import cn.master.zeus.common.exception.BusinessException;
import cn.master.zeus.dto.*;
import cn.master.zeus.dto.request.group.EditGroupRequest;
import cn.master.zeus.entity.SystemGroup;
import cn.master.zeus.entity.UserGroup;
import cn.master.zeus.entity.UserGroupPermission;
import cn.master.zeus.mapper.SystemGroupMapper;
import cn.master.zeus.mapper.UserGroupMapper;
import cn.master.zeus.mapper.UserGroupPermissionMapper;
import cn.master.zeus.service.SystemGroupService;
import cn.master.zeus.service.WorkspaceService;
import cn.master.zeus.util.JsonUtils;
import cn.master.zeus.util.SessionUtils;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import static cn.master.zeus.entity.table.ProjectTableDef.PROJECT;
import static cn.master.zeus.entity.table.SystemGroupTableDef.SYSTEM_GROUP;
import static cn.master.zeus.entity.table.UserGroupPermissionTableDef.USER_GROUP_PERMISSION;
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
public class SystemGroupServiceImpl extends ServiceImpl<SystemGroupMapper, SystemGroup> implements SystemGroupService {
    private static final String GLOBAL = "global";
    final WorkspaceService workspaceService;
    final UserGroupMapper userGroupMapper;
    final UserGroupPermissionMapper userGroupPermissionMapper;
    final StringRedisTemplate stringRedisTemplate;

    private static final Map<String, List<String>> MAP = new HashMap<>(4) {{
        put(UserGroupType.SYSTEM, Arrays.asList(UserGroupType.SYSTEM, UserGroupType.WORKSPACE, UserGroupType.PROJECT));
        put(UserGroupType.WORKSPACE, Arrays.asList(UserGroupType.WORKSPACE, UserGroupType.PROJECT));
        put(UserGroupType.PROJECT, Collections.singletonList(UserGroupType.PROJECT));
    }};

    @Override
    public List<Map<String, Object>> getAllUserGroup(String userId) {
        List<Map<String, Object>> list = new ArrayList<>();
        List<UserGroup> userGroups = QueryChain.of(UserGroup.class).where(USER_GROUP.USER_ID.eq(userId)).list();
        List<String> groupCodes = userGroups.stream().map(UserGroup::getGroupId).distinct().toList();
        groupCodes.forEach(gc -> {
            SystemGroup group = queryChain().where(SYSTEM_GROUP.ID.eq(gc)).one();
            String type = group.getType();
            Map<String, Object> map = new HashMap<>(2);
            map.put("type", gc + "+" + type);
            WorkspaceResource workspaceResource = workspaceService.listResource(gc, group.getType());
            //List<String> collect = userGroups.stream().map(UserGroup::getGroupCode).filter(groupCode -> groupCode.equals(gc)).toList();
            List<String> collect = userGroups.stream().filter(ugp -> ugp.getGroupId().equals(gc)).map(UserGroup::getSourceId).toList();
            map.put("ids", collect);
            if (StringUtils.equals(type, UserGroupType.WORKSPACE)) {
                map.put("workspaces", workspaceResource.getWorkspaces());
            }
            if (StringUtils.equals(type, UserGroupType.PROJECT)) {
                map.put("projects", workspaceResource.getProjects());
            }
            list.add(map);
        });
        return list;
    }

    @Override
    public List<SystemGroup> getGroupByType(EditGroupRequest request) {
        String type = request.getType();
        if (StringUtils.isBlank(type)) {
            return new ArrayList<>();
        }
        return mapper.selectListByQuery(queryChain().where(SYSTEM_GROUP.TYPE.eq(type).when(!StringUtils.equals(type, UserGroupType.SYSTEM))
                .and(SYSTEM_GROUP.SCOPE_ID.eq(GLOBAL).when(BooleanUtils.isTrue(request.isOnlyQueryGlobal())))));
    }

    @Override
    public Page<GroupDTO> getGroupPageList(EditGroupRequest request) {
        List<UserGroupDTO> userGroup = getUserGroupDTO(SessionUtils.getUserId(), request.getProjectId());
        List<String> groupTypeList = userGroup.stream().map(UserGroupDTO::getType).distinct().toList();
        return getGroups(groupTypeList, request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteGroup(Serializable id) {
        SystemGroup group = mapper.selectOneById(id);
        if (Objects.isNull(group)) {
           BusinessException.throwException("group does not exist!");
        }
        if (group.getSystem()) {
            BusinessException.throwException("系统用户组不支持删除");
        }
        mapper.deleteById(id);
        userGroupMapper.deleteByQuery(QueryChain.of(UserGroup.class).where(USER_GROUP.GROUP_ID.eq(id)));
        userGroupPermissionMapper.deleteByQuery(QueryChain.of(UserGroupPermission.class).where(USER_GROUP_PERMISSION.GROUP_ID.eq(id)));
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SystemGroup addGroup(EditGroupRequest request) {
        checkGroupExist(request);
        request.setSystem(false);
        request.setScopeId(Objects.nonNull(request.getGlobal()) && request.getGlobal() ? GLOBAL : request.getScopeId());
        request.setCreator(SessionUtils.getUserId());
        mapper.insert(request);
        return mapper.selectOneById(request.getId());
    }

    @Override
    public GroupPermissionDTO getGroupResource(SystemGroup group) {
        GroupPermissionDTO dto = new GroupPermissionDTO();
        List<UserGroupPermission> groupPermissions = QueryChain.of(UserGroupPermission.class).where(USER_GROUP_PERMISSION.GROUP_ID.eq(group.getId())).list();
        List<String> groupPermissionIds = groupPermissions.stream().map(UserGroupPermission::getPermissionId).toList();
        GroupJson groupJson = loadPermissionJsonFromService();
        List<GroupResourceDTO> dtoPermissions = dto.getPermissions();
        List<GroupResource> resource = groupJson.getResource();
        List<GroupPermission> permissions = groupJson.getPermissions();
        dtoPermissions.addAll(Objects.requireNonNull(getResourcePermission(resource, permissions, group, groupPermissionIds)));
        return dto;
    }

    @Override
    public void editGroupPermission(EditGroupRequest request) {
        // 超级用户组禁止修改权限
        if (StringUtils.equals(request.getUserGroupId(), UserGroupConstants.SUPER_GROUP)) {
            return;
        }
        List<GroupPermission> permissions = request.getPermissions();
        if (CollectionUtils.isEmpty(permissions)) {
            return;
        }
        userGroupPermissionMapper.deleteByQuery(QueryChain.of(UserGroupPermission.class)
                .where(USER_GROUP_PERMISSION.GROUP_ID.eq(request.getUserGroupId())));
        String groupId = request.getUserGroupId();
        permissions.forEach(permission -> {
            if (BooleanUtils.isTrue(permission.getChecked())) {
                String permissionId = permission.getId();
                String resourceId = permission.getResourceId();
                UserGroupPermission build = UserGroupPermission.builder().permissionId(permissionId).groupId(groupId).moduleId(resourceId).build();
                userGroupPermissionMapper.insert(build);
            }
        });

    }

    private List<GroupResourceDTO> getResourcePermission(List<GroupResource> resources, List<GroupPermission> permissions, SystemGroup group, List<String> permissionList) {
        List<GroupResourceDTO> dto = new ArrayList<>();
        List<GroupResource> grs;
        if (StringUtils.equals(group.getId(), UserGroupConstants.SUPER_GROUP)) {
            grs = resources;
            permissions.forEach(p -> p.setChecked(true));
        } else {
            grs = resources
                    .stream()
                    .filter(g -> g.getId().startsWith(group.getType()) || BooleanUtils.isTrue(g.isGlobal()))
                    .collect(Collectors.toList());
            permissions.forEach(p -> {
                if (permissionList.contains(p.getId())) {
                    p.setChecked(true);
                }
            });
        }

        for (GroupResource r : grs) {
            GroupResourceDTO resourceDTO = new GroupResourceDTO();
            resourceDTO.setResource(r);
            List<GroupPermission> collect = permissions
                    .stream()
                    .filter(p -> StringUtils.equals(r.getId(), p.getResourceId()))
                    .collect(Collectors.toList());
            resourceDTO.setPermissions(collect);
            resourceDTO.setType(r.getId().split("_")[0]);
            dto.add(resourceDTO);
        }
        return dto;
    }

    private GroupJson loadPermissionJsonFromService() {
        GroupJson groupJson;
        List<GroupResource> globalResources = new ArrayList<>();
        Object obj = stringRedisTemplate.opsForHash().get("PERMISSION", "service");
        GroupJson microServiceGroupJson = JsonUtils.parseObject((String) obj, GroupJson.class);
        assert microServiceGroupJson != null;
        List<GroupResource> globalResource = microServiceGroupJson.getResource().stream().filter(gp -> BooleanUtils.isTrue(gp.isGlobal())).toList();
        if (CollectionUtils.isNotEmpty(globalResource)) {
            globalResources.addAll(globalResource);
            microServiceGroupJson.getResource().removeIf(gp -> BooleanUtils.isTrue(gp.isGlobal()));
        }
        groupJson = microServiceGroupJson;
        // 拼装时通用权限Resource放在最后
        if (!globalResources.isEmpty()) {
            groupJson.getResource().addAll(globalResources);
        }
        return groupJson;
    }

    private void checkGroupExist(EditGroupRequest request) {
        boolean exists = queryChain().where(SYSTEM_GROUP.NAME.eq(request.getName()).and(SYSTEM_GROUP.ID.ne(request.getId()))).exists();
        if (exists) {
            BusinessException.throwException("用户组名称已存在");
        }
    }

    private Page<GroupDTO> getGroups(List<String> groupTypeList, EditGroupRequest request) {
        if (groupTypeList.contains(UserGroupType.SYSTEM)) {
            return getUserGroup(UserGroupType.SYSTEM, request);
        }
        if (groupTypeList.contains(UserGroupType.WORKSPACE)) {
            return getUserGroup(UserGroupType.WORKSPACE, request);
        }

        if (groupTypeList.contains(UserGroupType.PROJECT)) {
            return getUserGroup(UserGroupType.PROJECT, request);
        }
        return new Page<>();
    }

    private Page<GroupDTO> getUserGroup(String groupType, EditGroupRequest request) {
        List<String> types;
        String workspaceId = SessionUtils.getCurrentWorkspaceId();
        String projectId = SessionUtils.getCurrentProjectId();
        List<String> scopes = Arrays.asList(GLOBAL, workspaceId, projectId);
        if (StringUtils.equals(groupType, UserGroupType.SYSTEM)) {
            scopes = new ArrayList<>();
        }
        types = MAP.get(groupType);
        request.setTypes(types);
        request.setScopes(scopes);
        QueryChain<SystemGroup> queryChain = queryChain().select("*")
                .from(
                        queryChain().select(SYSTEM_GROUP.ALL_COLUMNS, WORKSPACE.NAME.as("scopeName"))
                                .join(WORKSPACE).on(SYSTEM_GROUP.SCOPE_ID.eq(WORKSPACE.ID))
                                .where(SYSTEM_GROUP.TYPE.in(request.getTypes()))
                                .union(
                                        queryChain().select(SYSTEM_GROUP.ALL_COLUMNS, SYSTEM_GROUP.SCOPE_ID.as("scopeName"))
                                                .from(SYSTEM_GROUP)
                                                .where(SYSTEM_GROUP.SCOPE_ID.eq("global")).and(SYSTEM_GROUP.TYPE.in(request.getTypes()))
                                ).union(
                                        queryChain().select(SYSTEM_GROUP.ALL_COLUMNS, SYSTEM_GROUP.SCOPE_ID.as("scopeName"))
                                                .from(SYSTEM_GROUP)
                                                .where(SYSTEM_GROUP.SCOPE_ID.eq("system")).and(SYSTEM_GROUP.TYPE.in(request.getTypes()))
                                ).union(
                                        queryChain().select(SYSTEM_GROUP.ALL_COLUMNS, PROJECT.NAME.as("scopeName"))
                                                .from(SYSTEM_GROUP)
                                                .join(PROJECT).on(SYSTEM_GROUP.SCOPE_ID.eq(PROJECT.ID))
                                                .where(SYSTEM_GROUP.TYPE.in(request.getTypes()).and(SYSTEM_GROUP.SCOPE_ID.in(request.getScopes())))
                                )
                ).as("tmp");
        Page<GroupDTO> page = mapper.paginateAs(Page.of(request.getPageNumber(), request.getPageSize()), queryChain, GroupDTO.class);
        // 人员数量
        List<GroupDTO> records = page.getRecords();
        if (CollectionUtils.isNotEmpty(records)) {
            records.forEach(groupDTO -> {
                        List<UserGroup> userGroups = QueryChain.of(UserGroup.class).select(distinct(USER_GROUP.USER_ID))
                                .where(USER_GROUP.GROUP_ID.eq(groupDTO.getId())).list();
                        groupDTO.setMemberSize(userGroups.size());
                    }
            );
        }
        return page;
    }

    private List<UserGroupDTO> getUserGroupDTO(String userId, String projectId) {
        QueryChain<SystemGroup> queryChain = queryChain().select(USER_GROUP.USER_ID, USER_GROUP.GROUP_ID, USER_GROUP.SOURCE_ID, SYSTEM_GROUP.NAME, SYSTEM_GROUP.TYPE.as("type"))
                .from(USER_GROUP).join(SYSTEM_GROUP).on(USER_GROUP.GROUP_ID.eq(SYSTEM_GROUP.ID))
                .where(USER_GROUP.USER_ID.eq(userId).and(USER_GROUP.SOURCE_ID.eq(projectId)));
        return mapper.selectListByQueryAs(queryChain, UserGroupDTO.class);
    }
}
