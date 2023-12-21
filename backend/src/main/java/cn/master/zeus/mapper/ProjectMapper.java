package cn.master.zeus.mapper;

import com.mybatisflex.core.BaseMapper;
import cn.master.zeus.entity.Project;
import org.apache.ibatis.annotations.Select;

/**
 *  映射层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
public interface ProjectMapper extends BaseMapper<Project> {

    @Select("select max(system_id) from project;")
    String getMaxSystemId();
}
