package cn.master.zeus.controller;

import cn.master.zeus.dto.GroupDTO;
import cn.master.zeus.dto.GroupPermissionDTO;
import cn.master.zeus.dto.request.group.EditGroupRequest;
import cn.master.zeus.entity.SystemGroup;
import cn.master.zeus.service.SystemGroupService;
import com.mybatisflex.core.paginate.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 控制层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@RestController
@RequestMapping("/user/group")
@RequiredArgsConstructor
public class SystemGroupController {

    private final SystemGroupService systemGroupService;

    /**
     * 添加。
     *
     * @param systemGroup
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public SystemGroup save(@RequestBody EditGroupRequest systemGroup) {
        return systemGroupService.addGroup(systemGroup);
    }

    /**
     * 根据主键删除。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @GetMapping("remove/{id}")
    public boolean remove(@PathVariable Serializable id) {
        return systemGroupService.deleteGroup(id);
    }

    /**
     * 根据主键更新。
     *
     * @param systemGroup
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody SystemGroup systemGroup) {
        return systemGroupService.updateById(systemGroup);
    }

    /**
     * 查询所有。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<SystemGroup> list() {
        return systemGroupService.list();
    }

    /**
     * 根据主键获取详细信息。
     *
     * @param id 主键
     * @return 详情
     */
    @GetMapping("getInfo/{id}")
    public SystemGroup getInfo(@PathVariable Serializable id) {
        return systemGroupService.getById(id);
    }

    /**
     * 分页查询。
     *
     * @param request 分页对象
     * @return 分页对象
     */
    @PostMapping("page")
    public Page<GroupDTO> page(@RequestBody EditGroupRequest request) {
        return systemGroupService.getGroupPageList(request);
    }

    @GetMapping("/all/{userId}")
    public List<Map<String, Object>> getAllUserGroup(@PathVariable("userId") String userId) {
        return systemGroupService.getAllUserGroup(userId);
    }

    @PostMapping("/get")
    public List<SystemGroup> get(@RequestBody EditGroupRequest request) {
        return systemGroupService.getGroupByType(request);
    }

    @PostMapping("/permission")
    public GroupPermissionDTO getGroupResource(@RequestBody SystemGroup group) {
        return systemGroupService.getGroupResource(group);
    }
    @PostMapping("/permission/edit")
    public void editGroupPermission(@RequestBody EditGroupRequest editGroupRequest) {
        systemGroupService.editGroupPermission(editGroupRequest);
    }
}
