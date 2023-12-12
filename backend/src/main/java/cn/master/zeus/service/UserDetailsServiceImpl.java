package cn.master.zeus.service;

import cn.master.zeus.dto.CustomUserDetails;
import cn.master.zeus.entity.SystemUser;
import com.mybatisflex.core.query.QueryChain;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static cn.master.zeus.entity.table.SystemUserTableDef.SYSTEM_USER;

/**
 * @author Created by 11's papa on 12/10/2023
 **/
@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        val user = QueryChain.of(SystemUser.class).where(SYSTEM_USER.NAME.eq(username)).one();
        if (Objects.isNull(user)) {
            throw new UsernameNotFoundException("User " + username + " not found");
        }
        List<String> roles = new ArrayList<>();
        roles.add("USER");
        List<GrantedAuthority> authorities = new ArrayList<>();
        roles.forEach(r -> {
            authorities.add(new SimpleGrantedAuthority(r));
        });
        val userDetails = new CustomUserDetails(user, authorities);
        try {
            new AccountStatusUserDetailsChecker().check(userDetails);
        } catch (AccountStatusException e) {
            log.error("Could not authenticate user", e);
            throw new RuntimeException(e.getMessage());
        }
        return userDetails;
    }
}
