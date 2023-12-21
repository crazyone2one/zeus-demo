package cn.master.zeus.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.zeus.entity.ProjectVersion;
import cn.master.zeus.mapper.ProjectVersionMapper;
import cn.master.zeus.service.ProjectVersionService;
import org.springframework.stereotype.Service;

/**
 *  服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
public class ProjectVersionServiceImpl extends ServiceImpl<ProjectVersionMapper, ProjectVersion> implements ProjectVersionService {

}
