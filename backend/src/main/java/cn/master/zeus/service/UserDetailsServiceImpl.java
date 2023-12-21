package cn.master.zeus.service;

import cn.master.zeus.config.SpringContextHolder;
import cn.master.zeus.dto.CustomUserDetails;
import cn.master.zeus.dto.GroupResourceDTO;
import cn.master.zeus.entity.SystemUser;
import cn.master.zeus.entity.UserGroupPermission;
import com.mybatisflex.core.query.QueryChain;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.collections4.CollectionUtils;
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
import java.util.stream.Collectors;

import static cn.master.zeus.entity.table.SystemUserTableDef.SYSTEM_USER;

/**
 * @author Created by 11's papa on 12/10/2023
 **/
@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) {
        val user = QueryChain.of(SystemUser.class).where(SYSTEM_USER.NAME.eq(username)).one();
        if (Objects.isNull(user)) {
            throw new UsernameNotFoundException("User " + username + " not found");
        }
        val userDTO = SpringContextHolder.getBean(AuthenticationService.class).getUserInfo(user.getId());
        List<String> roles = new ArrayList<>();
        List<GroupResourceDTO> groupPermissions = userDTO.getGroupPermissions();
        if (CollectionUtils.isNotEmpty(groupPermissions)) {
            List<List<UserGroupPermission>> list = groupPermissions.stream().map(GroupResourceDTO::getUserGroupPermissions).toList();
            if (CollectionUtils.isNotEmpty(list)) {
                list.forEach(p -> {
                            List<String> list1 = p.stream().map(UserGroupPermission::getPermissionId).toList();
                            roles.addAll(list1);
                        }
                );
            }
        }
        List<GrantedAuthority> authorities = roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
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
