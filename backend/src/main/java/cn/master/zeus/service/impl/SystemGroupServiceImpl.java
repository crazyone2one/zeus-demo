package cn.master.zeus.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.zeus.entity.SystemGroup;
import cn.master.zeus.mapper.SystemGroupMapper;
import cn.master.zeus.service.SystemGroupService;
import org.springframework.stereotype.Service;

/**
 *  服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
public class SystemGroupServiceImpl extends ServiceImpl<SystemGroupMapper, SystemGroup> implements SystemGroupService {

}
