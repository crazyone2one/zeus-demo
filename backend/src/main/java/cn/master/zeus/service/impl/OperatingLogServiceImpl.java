package cn.master.zeus.service.impl;

import cn.master.zeus.entity.OperatingLogResource;
import cn.master.zeus.mapper.OperatingLogResourceMapper;
import cn.master.zeus.util.JsonUtils;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.zeus.entity.OperatingLog;
import cn.master.zeus.mapper.OperatingLogMapper;
import cn.master.zeus.service.OperatingLogService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class OperatingLogServiceImpl extends ServiceImpl<OperatingLogMapper, OperatingLog> implements OperatingLogService {
    final OperatingLogResourceMapper operatingLogResourceMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(OperatingLog log, String sourceIds) {
        log.setSourceId(StringUtils.EMPTY);
        mapper.insert(log);
        if (StringUtils.isNotEmpty(sourceIds)) {
            List<String> ids = new ArrayList<>();
            if (sourceIds.startsWith("[")) {
                ids = JsonUtils.parseArray(sourceIds, String.class);
            } else {
                ids.add(sourceIds.replace("\"", StringUtils.EMPTY));
            }
            if (CollectionUtils.isNotEmpty(ids)) {
                ids.forEach(item -> {
                    val build = OperatingLogResource.builder().operatingLogId(log.getId())
                            .sourceId(item).build();
                    operatingLogResourceMapper.insert(build);
                });
            }
        }
    }
}
