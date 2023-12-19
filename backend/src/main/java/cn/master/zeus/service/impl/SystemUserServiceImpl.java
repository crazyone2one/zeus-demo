package cn.master.zeus.service.impl;

import cn.master.zeus.dto.request.QueryMemberRequest;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.zeus.entity.SystemUser;
import cn.master.zeus.mapper.SystemUserMapper;
import cn.master.zeus.service.SystemUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.master.zeus.entity.table.SystemUserTableDef.SYSTEM_USER;
import static cn.master.zeus.entity.table.UserGroupTableDef.USER_GROUP;

/**
 *  服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
public class SystemUserServiceImpl extends ServiceImpl<SystemUserMapper, SystemUser> implements SystemUserService {

    @Override
    public List<SystemUser> getMemberList(QueryMemberRequest request) {
        QueryWrapper wrapper = new QueryWrapper();
        QueryWrapper query = new QueryWrapper()
                .select("distinct *").from(
                        wrapper.select(SYSTEM_USER.ALL_COLUMNS)
                                .from(USER_GROUP).join(SYSTEM_USER).on(USER_GROUP.USER_ID.eq(SYSTEM_USER.ID))
                                .where(USER_GROUP.SOURCE_ID.eq(request.getWorkspaceId()))
                                .and(SYSTEM_USER.NAME.like(request.getName(), StringUtils.isNoneBlank(request.getName())))
                                .orderBy(USER_GROUP.UPDATE_TIME.desc())
                ).as("temp");
        return mapper.selectListByQuery(query);
    }
}
