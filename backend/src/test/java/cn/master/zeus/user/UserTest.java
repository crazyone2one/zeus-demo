package cn.master.zeus.user;

import cn.master.zeus.entity.SystemUser;
import cn.master.zeus.mapper.SystemUserMapper;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Created by 11's papa on 12/10/2023
 **/
@SpringBootTest
public class UserTest {
    @Autowired
    private SystemUserMapper userMapper;

    @Test
    void TestSql() {
        val administrator = SystemUser.builder().name("administrator")
                .email("administrator@master.cn").status("0")
                .password("123456").build();
        userMapper.insert(administrator);
    }
}
