package cn.master.zeus.user;

import cn.master.zeus.entity.SystemUser;
import cn.master.zeus.mapper.SystemUserMapper;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author Created by 11's papa on 12/10/2023
 **/
@SpringBootTest
public class UserTest {
    @Autowired
    private SystemUserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void TestSql() {
        val administrator = SystemUser.builder().name("administrator")
                .email("administrator@master.cn")
                .password(passwordEncoder.encode("Passw0rd")).build();
        userMapper.insert(administrator);
    }
}
