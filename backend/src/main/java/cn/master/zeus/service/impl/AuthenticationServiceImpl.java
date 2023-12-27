package cn.master.zeus.service.impl;

import cn.master.zeus.auth.JwtProvider;
import cn.master.zeus.common.enums.TokenType;
import cn.master.zeus.dto.CustomUserDetails;
import cn.master.zeus.dto.GroupResourceDTO;
import cn.master.zeus.dto.UserDTO;
import cn.master.zeus.dto.UserGroupPermissionDTO;
import cn.master.zeus.dto.request.AuthenticationRequest;
import cn.master.zeus.dto.request.RefreshTokenRequest;
import cn.master.zeus.dto.response.AuthenticationResponse;
import cn.master.zeus.entity.*;
import cn.master.zeus.mapper.SystemTokenMapper;
import cn.master.zeus.mapper.UserGroupPermissionMapper;
import cn.master.zeus.service.AuthenticationService;
import com.mybatisflex.core.query.QueryChain;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.master.zeus.entity.table.SystemGroupTableDef.SYSTEM_GROUP;
import static cn.master.zeus.entity.table.SystemTokenTableDef.SYSTEM_TOKEN;
import static cn.master.zeus.entity.table.SystemUserTableDef.SYSTEM_USER;
import static cn.master.zeus.entity.table.UserGroupPermissionTableDef.USER_GROUP_PERMISSION;
import static cn.master.zeus.entity.table.UserGroupTableDef.USER_GROUP;

/**
 * @author Created by 11's papa on 12/12/2023
 **/
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    final UserGroupPermissionMapper userGroupPermissionMapper;
    final SystemTokenMapper tokenMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuthenticationResponse login(AuthenticationRequest request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        val user = QueryChain.of(SystemUser.class).where(SYSTEM_USER.NAME.eq(request.getUsername())).one();
        val userDTO = getUserInfo(user.getId());
        CustomUserDetails customUserDetails = getCustomUserDetails(userDTO, user);
        val accessToken = jwtProvider.generateAccessToken(customUserDetails);
        val refreshToken = jwtProvider.generateRefreshToken(customUserDetails);
        revokeUserToken(customUserDetails.getSystemUser(), Arrays.asList(TokenType.ACCESS_TOKEN, TokenType.REFRESH_TOKEN));
        saveUserToken(customUserDetails.getSystemUser(), accessToken, TokenType.ACCESS_TOKEN.name());
        saveUserToken(customUserDetails.getSystemUser(), refreshToken, TokenType.REFRESH_TOKEN.name());
        val userInfo = getUserInfo(customUserDetails.getSystemUser().getId());
        return AuthenticationResponse.builder()
                .accessToken(accessToken).refreshToken(refreshToken)
                .userId(customUserDetails.getSystemUser().getId())
                .user(userInfo)
                .build();
    }


    private CustomUserDetails getCustomUserDetails(UserDTO userDTO, SystemUser user) {
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
        return new CustomUserDetails(user, authorities);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void logout(String userName) {
        val user = QueryChain.of(SystemUser.class).where(SYSTEM_USER.NAME.eq(userName)).one();
        revokeUserToken(user, Arrays.asList(TokenType.ACCESS_TOKEN, TokenType.REFRESH_TOKEN));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuthenticationResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        val claimsJws = jwtProvider.validateToken(refreshToken);
        Claims body = claimsJws.getBody();
        val user = QueryChain.of(SystemUser.class).where(SYSTEM_USER.NAME.eq(body.get("username"))).one();
        val userDTO = getUserInfo(user.getId());
        CustomUserDetails customUserDetails = getCustomUserDetails(userDTO, user);
        val accessToken = jwtProvider.generateAccessToken(customUserDetails);
        revokeUserToken(user, List.of(TokenType.ACCESS_TOKEN));
        saveUserToken(user, accessToken, TokenType.ACCESS_TOKEN.name());
        val userInfo = getUserInfo(user.getId());
        return AuthenticationResponse.builder()
                .accessToken(accessToken).refreshToken(refreshToken)
                .userId(user.getId())
                .user(userInfo)
                .build();
    }

    @Override
    public UserDTO getUserInfo(String userId) {
        val user = QueryChain.of(SystemUser.class).where(SYSTEM_USER.ID.eq(userId)).one();
        if (Objects.isNull(user)) {
            return null;
        }
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        // 获取用户角色
        UserGroupPermissionDTO dto = getUserGroupPermission(userId);
        userDTO.setUserGroups(dto.getUserGroups());
        userDTO.setGroups(dto.getGroups());
        userDTO.setGroupPermissions(dto.getList());
        return userDTO;
    }

    private UserGroupPermissionDTO getUserGroupPermission(String userId) {
        UserGroupPermissionDTO permissionDTO = new UserGroupPermissionDTO();
        List<GroupResourceDTO> list = new ArrayList<>();
        val userGroups = QueryChain.of(UserGroup.class).where(USER_GROUP.USER_ID.eq(userId)).list();
        if (CollectionUtils.isEmpty(userGroups)) {
            return permissionDTO;
        }
        permissionDTO.setUserGroups(userGroups);
        List<String> groupList = userGroups.stream().map(UserGroup::getGroupId).toList();
        val groups = QueryChain.of(SystemGroup.class).where(SYSTEM_GROUP.ID.in(groupList)).list();
        permissionDTO.setGroups(groups);
        for (SystemGroup gp : groups) {
            GroupResourceDTO dto = new GroupResourceDTO();
            dto.setGroup(gp);
            val userGroupPermissions = QueryChain.of(UserGroupPermission.class).where(USER_GROUP_PERMISSION.GROUP_ID.eq(gp.getId())).list();
            dto.setUserGroupPermissions(userGroupPermissions);
            list.add(dto);
        }
        permissionDTO.setList(list);
        return permissionDTO;
    }

    protected void saveUserToken(SystemUser user, String token, String tokenType) {
        val systemToken = SystemToken.builder().token(token).tokenType(tokenType).userId(user.getId()).revoked(false).build();
        tokenMapper.insert(systemToken);
    }

    protected void revokeUserToken(SystemUser user, List<TokenType> tokenTypes) {
        tokenTypes.forEach(tt -> {
            val systemToken = QueryChain.of(SystemToken.class).where(
                    SYSTEM_TOKEN.USER_ID.eq(user.getId())
                            .and(SYSTEM_TOKEN.TOKEN_TYPE.eq(tt.name()))
                            .and(SYSTEM_TOKEN.REVOKED.eq(false))
            ).list();
            if (CollectionUtils.isNotEmpty(systemToken)) {
                systemToken.forEach(st -> {
                    st.setRevoked(true);
                    tokenMapper.update(st);
                });
            }
        });
    }
}
