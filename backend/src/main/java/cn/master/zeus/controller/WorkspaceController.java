package cn.master.zeus.controller;

import cn.master.zeus.common.annotation.LogAnnotation;
import cn.master.zeus.common.constants.OperateLogConstants;
import cn.master.zeus.common.constants.OperateLogModule;
import cn.master.zeus.dto.request.BaseRequest;
import com.mybatisflex.core.paginate.Page;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import cn.master.zeus.entity.Workspace;
import cn.master.zeus.service.WorkspaceService;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.List;

/**
 * 控制层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@RestController
@RequestMapping("/workspace")
@RequiredArgsConstructor
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    /**
     * 添加。
     *
     * @param workspace param
     * @return {@code Workspace} 添加成功
     */
    @PostMapping("special/add")
    @LogAnnotation(module = OperateLogModule.SYSTEM_WORKSPACE, type = OperateLogConstants.CREATE, msClass = WorkspaceService.class, content = "#msClass.getLogDetails(#workspace.id)")
    public Workspace save(@RequestBody @Valid Workspace workspace) {
        return workspaceService.addWorkspaceByAdmin(workspace);
    }

    /**
     * 根据主键删除。
     *
     * @param workspaceId 主键
     */
    @DeleteMapping("special/delete/{workspaceId}")
    public void remove(@PathVariable String workspaceId) {
        workspaceService.deleteWorkspace(workspaceId);
    }

    /**
     * 根据主键更新。
     *
     * @param workspace param
     */
    @PutMapping("special/update")
    public void update(@RequestBody @Valid Workspace workspace) {
        workspaceService.updateWorkspaceByAdmin(workspace);
    }

    /**
     * 查询所有。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<Workspace> list() {
        return workspaceService.list();
    }

    /**
     * 根据主键获取详细信息。
     *
     * @param id 主键
     * @return 详情
     */
    @GetMapping("getInfo/{id}")
    public Workspace getInfo(@PathVariable Serializable id) {
        return workspaceService.getById(id);
    }

    /**
     * 分页查询。
     *
     * @param request 分页对象
     * @return 分页对象
     */
    @PostMapping("page")
    public Page<Workspace> page(@RequestBody BaseRequest request) {
        return workspaceService.getAllWorkspaceList(request);
    }

}
