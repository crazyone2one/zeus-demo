package cn.master.zeus.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.zeus.entity.UserGroupPermission;
import cn.master.zeus.mapper.UserGroupPermissionMapper;
import cn.master.zeus.service.UserGroupPermissionService;
import org.springframework.stereotype.Service;

/**
 *  服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
public class UserGroupPermissionServiceImpl extends ServiceImpl<UserGroupPermissionMapper, UserGroupPermission> implements UserGroupPermissionService {

}
