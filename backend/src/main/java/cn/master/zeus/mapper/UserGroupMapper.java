package cn.master.zeus.mapper;

import com.mybatisflex.core.BaseMapper;
import cn.master.zeus.entity.UserGroup;
import org.apache.ibatis.annotations.Select;

/**
 *  映射层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
public interface UserGroupMapper extends BaseMapper<UserGroup> {
    @Select("select count(*) from user_group where user_id = #{userId} and group_id = 'super_group'")
    boolean isSuperUser(String userId);
}
