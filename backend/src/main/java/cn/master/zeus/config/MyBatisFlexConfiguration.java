package cn.master.zeus.config;

import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.audit.AuditManager;
import com.mybatisflex.core.audit.ConsoleMessageCollector;
import com.mybatisflex.core.audit.MessageCollector;
import com.mybatisflex.core.keygen.KeyGenerators;
import com.mybatisflex.core.mybatis.FlexConfiguration;
import com.mybatisflex.spring.boot.ConfigurationCustomizer;
import com.mybatisflex.spring.boot.MyBatisFlexCustomizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/**
 * @author Created by 11's papa on 12/10/2023
 **/
@Slf4j
@Configuration
public class MyBatisFlexConfiguration implements MyBatisFlexCustomizer, ConfigurationCustomizer {

    @Override
    public void customize(FlexGlobalConfig globalConfig) {
        FlexGlobalConfig.KeyConfig keyConfig = new FlexGlobalConfig.KeyConfig();
        keyConfig.setKeyType(KeyType.Generator);
        keyConfig.setValue(KeyGenerators.flexId);
        keyConfig.setBefore(true);
        FlexGlobalConfig.getDefaultConfig().setKeyConfig(keyConfig);
        //全局配置逻辑删除字段
        FlexGlobalConfig.getDefaultConfig().setLogicDeleteColumn("del_flag");
    }

    @Override
    public void customize(FlexConfiguration configuration) {
        //开启审计功能
        AuditManager.setAuditEnable(true);

        //设置 SQL 审计收集器
        MessageCollector collector = new ConsoleMessageCollector();
        AuditManager.setMessageCollector(collector);
    }
}
