package cn.master.zeus.controller;

import cn.master.zeus.dto.request.ProjectRequest;
import cn.master.zeus.entity.Project;
import cn.master.zeus.service.ProjectService;
import cn.master.zeus.util.SessionUtils;
import com.mybatisflex.core.paginate.Page;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/project")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    /**
     * 添加。
     *
     * @param project
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    @PreAuthorize("hasAuthority('WORKSPACE_PROJECT_MANAGER:READ+CREATE')")
    public Project save(@RequestBody @Valid Project project) {
        return projectService.addProject(project);
    }

    /**
     * 根据主键删除。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    @PreAuthorize("hasAuthority('WORKSPACE_PROJECT_MANAGER:READ+DELETE')")
    public int remove(@PathVariable Serializable id) {
        return projectService.deleteProject(id);
    }

    /**
     * 根据主键更新。
     *
     * @param project
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PostMapping("update")
    @PreAuthorize("hasAuthority('WORKSPACE_PROJECT_MANAGER:READ+EDIT')")
    public boolean update(@RequestBody Project project) {
        return projectService.updateById(project);
    }

    /**
     * 查询所有。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    @PreAuthorize("hasAuthority('WORKSPACE_PROJECT_MANAGER:READ')")
    public List<Project> list() {
        return projectService.list();
    }

    /**
     * 根据主键获取详细信息。
     *
     * @param id 主键
     * @return 详情
     */
    @GetMapping("getInfo/{id}")
    @PreAuthorize("hasAuthority('WORKSPACE_PROJECT_MANAGER:READ')")
    public Project getInfo(@PathVariable Serializable id) {
        return projectService.getById(id);
    }

    /**
     * 分页查询。
     *
     * @param request 分页对象
     * @return 分页对象
     */
    @PostMapping("page")
    @PreAuthorize("hasAuthority('WORKSPACE_PROJECT_MANAGER:READ')")
    public Page<Project> page(@RequestBody ProjectRequest request) {
        if (StringUtils.isBlank(request.getWorkspaceId())) {
            return new Page<>();
        }
        return projectService.getProjectPageList(request);
    }
    @GetMapping("/member/size/{id}")
    public long getProjectMemberSize(@PathVariable String id){
        return projectService.getProjectMemberSize(id);
    }

    @PostMapping("/list/related")
    public List<Project> getUserProject(@RequestBody ProjectRequest request) {
        request.setUserId(SessionUtils.getUserId());
        return projectService.getUserProject(request);
    }

}
