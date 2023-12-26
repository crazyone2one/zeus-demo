package cn.master.zeus.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.InputStream;

/**
 * @author Created by 11's papa on 12/25/2023
 **/
@Slf4j
@Configuration
@RequiredArgsConstructor
public class PermissionConfig implements ApplicationRunner {
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("初始化权限配置");
        InputStream inputStream = PermissionConfig.class.getResourceAsStream("/permission.json");
        assert inputStream != null;
        String permission = IOUtils.toString(inputStream);
        stringRedisTemplate.opsForHash().put("PERMISSION", "service", permission);
        log.info("初始化权限配置完成");
    }
}