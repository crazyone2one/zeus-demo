package cn.master.zeus.util;

import com.alibaba.druid.pool.DruidDataSource;
import com.mybatisflex.codegen.Generator;
import com.mybatisflex.codegen.config.ColumnConfig;
import com.mybatisflex.codegen.config.GlobalConfig;
import com.mybatisflex.codegen.dialect.IDialect;

import javax.sql.DataSource;

/**
 * @author Created by 11's papa on 12/10/2023
 **/
public class Codegen {
    private static DataSource dataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mysql://127.0.0.1:3306/zeus?characterEncoding=UTF-8&useInformationSchema=true");
        dataSource.setUsername("root");
        dataSource.setPassword("admin");
        return dataSource;
    }
    public static GlobalConfig createGlobalConfig() {
        //创建配置内容
        GlobalConfig globalConfig = new GlobalConfig();

        //设置根包
        globalConfig.getPackageConfig()
                .setBasePackage("cn.master.zeus")
                .setSourceDir(System.getProperty("user.dir") + "/backend/src/main/java");

        //设置表前缀和只生成哪些表，setGenerateTable 未配置时，生成所有表
        globalConfig.getStrategyConfig()
                .setTablePrefix("tb_")
                .setGenerateTable("system_user");

        globalConfig.enableController();
        //设置生成 entity 并启用 Lombok
        globalConfig.enableEntity()
                .setWithLombok(true).setOverwriteEnable(false);
        globalConfig.enableService();
        globalConfig.enableServiceImpl();
        //设置生成 mapper
        globalConfig.enableMapper();
//        globalConfig.enableMapperXml();
        // 注释配置 JavadocConfig
        globalConfig.getJavadocConfig().setAuthor("11's papa").setSince("1.0.0");
        //可以单独配置某个列
//        ColumnConfig columnConfig = new ColumnConfig();
//        columnConfig.setColumnName("tenant_id");
//        columnConfig.setLarge(true);
//        columnConfig.setVersion(true);
//        globalConfig.getStrategyConfig()
//                .setColumnConfig("tb_account", columnConfig);

        return globalConfig;
    }

    public static void main(String[] args) {
        Generator generator = new Generator(dataSource(), createGlobalConfig(), IDialect.MYSQL);
        generator.generate();
    }
}
