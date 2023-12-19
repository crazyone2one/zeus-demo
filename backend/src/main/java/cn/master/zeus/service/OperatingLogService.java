package cn.master.zeus.service;

import com.mybatisflex.core.service.IService;
import cn.master.zeus.entity.OperatingLog;

/**
 *  服务层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
public interface OperatingLogService extends IService<OperatingLog> {

    void create(OperatingLog operatingLog, String sourceId);
}
