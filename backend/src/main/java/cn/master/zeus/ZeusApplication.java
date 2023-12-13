package cn.master.zeus;

import cn.master.zeus.config.RsaKeyConfigProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @author 11's papa
 */
@SpringBootApplication
@MapperScan("cn.master.zeus.mapper")
@EnableConfigurationProperties(RsaKeyConfigProperties.class)
public class ZeusApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZeusApplication.class, args);
    }

}
