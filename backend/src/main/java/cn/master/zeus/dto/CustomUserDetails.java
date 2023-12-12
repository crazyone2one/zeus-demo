package cn.master.zeus.dto;

import cn.master.zeus.entity.SystemUser;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Created by 11's papa on 12/12/2023
 **/
@Getter
public class CustomUserDetails implements UserDetails {
    private final SystemUser systemUser;
    private final List<GrantedAuthority> authorities;

    public CustomUserDetails(SystemUser systemUser) {
        this(systemUser, Collections.emptyList());
    }

    public CustomUserDetails(SystemUser systemUser, List<GrantedAuthority> permissions) {
        this.systemUser = systemUser;
        this.authorities = permissions;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return systemUser.getPassword();
    }

    @Override
    public String getUsername() {
        return systemUser.getName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return systemUser.isStatus();
    }

    @Override
    public boolean isAccountNonLocked() {
        return systemUser.isStatus();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return systemUser.isStatus();
    }

    @Override
    public boolean isEnabled() {
        return systemUser.isStatus();
    }
}
