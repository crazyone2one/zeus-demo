package cn.master.zeus.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.zeus.entity.SystemUser;
import cn.master.zeus.mapper.SystemUserMapper;
import cn.master.zeus.service.SystemUserService;
import org.springframework.stereotype.Service;

/**
 *  服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
public class SystemUserServiceImpl extends ServiceImpl<SystemUserMapper, SystemUser> implements SystemUserService {

}
