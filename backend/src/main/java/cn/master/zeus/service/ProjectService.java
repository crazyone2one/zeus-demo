package cn.master.zeus.service;

import cn.master.zeus.dto.request.ProjectRequest;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import cn.master.zeus.entity.Project;

import java.io.Serializable;
import java.util.List;

/**
 *  服务层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
public interface ProjectService extends IService<Project> {

    Project addProject(Project project);

    Page<Project> getProjectPageList(ProjectRequest request);

    int deleteProject(Serializable id);

    long getProjectMemberSize(String id);

    List<Project> getUserProject(ProjectRequest request);
}
