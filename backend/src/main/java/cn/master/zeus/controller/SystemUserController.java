package cn.master.zeus.controller;

import cn.master.zeus.dto.UserDTO;
import cn.master.zeus.dto.UserGroupPermissionDTO;
import cn.master.zeus.dto.request.EditPasswordRequest;
import cn.master.zeus.dto.request.QueryMemberRequest;
import cn.master.zeus.dto.request.member.UserRequest;
import cn.master.zeus.entity.SystemUser;
import cn.master.zeus.service.BaseCheckPermissionService;
import cn.master.zeus.service.SystemUserService;
import com.mybatisflex.core.paginate.Page;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * 控制层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class SystemUserController {

    private final SystemUserService systemUserService;
    final BaseCheckPermissionService baseCheckPermissionService;

    /**
     * 添加。
     *
     * @param systemUser
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public UserDTO save(@RequestBody @Valid UserRequest systemUser) {
        return systemUserService.insert(systemUser);
    }

    /**
     * 根据主键删除。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Serializable id) {
        return systemUserService.removeById(id);
    }

    /**
     * 根据主键更新。
     *
     * @param systemUser
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PostMapping("update")
    public String update(@RequestBody UserRequest systemUser) {
        return systemUserService.updateUser(systemUser);
    }

    /**
     * 更新用户状态
     *
     * @param systemUser
     * @return java.lang.String
     */
    @PostMapping("/special/update_status")
    public String updateStatus(@RequestBody SystemUser systemUser) {
        return systemUserService.updateStatus(systemUser);
    }

    /**
     * 管理员修改用户密码
     *
     * @param request
     * @return int
     */
    @PostMapping("/special/password")
    public int updateUserPassword(@RequestBody EditPasswordRequest request) {
        return systemUserService.updateUserPassword(request);
    }

    /**
     * 查询所有。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<SystemUser> list() {
        return systemUserService.list();
    }

    /**
     * 根据主键获取详细信息。
     *
     * @param id 主键
     * @return 详情
     */
    @GetMapping("getInfo/{id}")
    public SystemUser getInfo(@PathVariable Serializable id) {
        return systemUserService.getById(id);
    }

    /**
     * 分页查询。
     *
     * @param request 分页对象
     * @return 分页对象
     */
    @PostMapping("page")
    public Page<SystemUser> page(@RequestBody cn.master.zeus.dto.request.UserRequest request) {
        return systemUserService.getUserPageData(request);
    }

    @GetMapping("/switch/source/ws/{sourceId}")
    public UserDTO switchWorkspace(@PathVariable(value = "sourceId") String sourceId) {
        val principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return systemUserService.switchUserResource("workspace", sourceId, principal);
    }

    @PostMapping("/ws/project/member/list/{workspaceId}")
    public List<SystemUser> getProjectMemberListForWorkspace(@PathVariable String workspaceId, @RequestBody QueryMemberRequest request) {
        baseCheckPermissionService.checkProjectBelongToWorkspace(request.getProjectId(), workspaceId);
        return systemUserService.getProjectMemberList(request);
    }

    @GetMapping("/special/user/group/{userId}")
    public UserGroupPermissionDTO getUserGroup(@PathVariable String userId) {
        return systemUserService.getUserGroup(userId);
    }
}
