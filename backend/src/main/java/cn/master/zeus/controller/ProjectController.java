package cn.master.zeus.controller;

import cn.master.zeus.dto.request.ProjectRequest;
import com.mybatisflex.core.paginate.Page;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import cn.master.zeus.entity.Project;
import cn.master.zeus.service.ProjectService;
import org.springframework.web.bind.annotation.RestController;
import java.io.Serializable;
import java.util.List;

/**
 *  控制层。
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
    public boolean remove(@PathVariable Serializable id) {
        return projectService.removeById(id);
    }

    /**
     * 根据主键更新。
     *
     * @param project 
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody Project project) {
        return projectService.updateById(project);
    }

    /**
     * 查询所有。
     *
     * @return 所有数据
     */
    @GetMapping("list")
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
    public Page<Project> page(@RequestBody ProjectRequest request) {
        if (StringUtils.isBlank(request.getWorkspaceId())) {
            return new Page<>();
        }
        return projectService.getProjectPageList(request);
    }

}
