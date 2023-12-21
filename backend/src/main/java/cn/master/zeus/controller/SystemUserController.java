package cn.master.zeus.controller;

import cn.master.zeus.dto.CustomUserDetails;
import cn.master.zeus.dto.UserDTO;
import cn.master.zeus.dto.request.QueryMemberRequest;
import cn.master.zeus.service.BaseCheckPermissionService;
import cn.master.zeus.util.SessionUtils;
import com.mybatisflex.core.paginate.Page;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import cn.master.zeus.entity.SystemUser;
import cn.master.zeus.service.SystemUserService;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

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
    public boolean save(@RequestBody SystemUser systemUser) {
        return systemUserService.save(systemUser);
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
    @PutMapping("update")
    public boolean update(@RequestBody SystemUser systemUser) {
        return systemUserService.updateById(systemUser);
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
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<SystemUser> page(Page<SystemUser> page) {
        return systemUserService.page(page);
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
}
