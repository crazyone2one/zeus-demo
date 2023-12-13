package cn.master.zeus.service.impl;

import cn.master.zeus.auth.JwtProvider;
import cn.master.zeus.common.enums.TokenType;
import cn.master.zeus.dto.CustomUserDetails;
import cn.master.zeus.dto.request.AuthenticationRequest;
import cn.master.zeus.dto.request.RefreshTokenRequest;
import cn.master.zeus.dto.response.AuthenticationResponse;
import cn.master.zeus.entity.SystemToken;
import cn.master.zeus.entity.SystemUser;
import cn.master.zeus.mapper.SystemTokenMapper;
import cn.master.zeus.service.AuthenticationService;
import com.mybatisflex.core.query.QueryChain;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static cn.master.zeus.entity.table.SystemTokenTableDef.SYSTEM_TOKEN;
import static cn.master.zeus.entity.table.SystemUserTableDef.SYSTEM_USER;

/**
 * @author Created by 11's papa on 12/12/2023
 **/
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    final SystemTokenMapper tokenMapper;
    @Value("${security.jwt.accessToken.expiration:86400000}")
    private Integer jwtExpiration;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuthenticationResponse login(AuthenticationRequest request) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        val principal = (CustomUserDetails)authentication.getPrincipal();
        val accessToken = jwtProvider.generateAccessToken( principal);
        val refreshToken = jwtProvider.generateRefreshToken( principal);
        revokeUserToken(principal.getSystemUser(), Arrays.asList(TokenType.ACCESS_TOKEN, TokenType.REFRESH_TOKEN));
        saveUserToken(principal.getSystemUser(), accessToken, TokenType.ACCESS_TOKEN.name());
        saveUserToken(principal.getSystemUser(), refreshToken, TokenType.REFRESH_TOKEN.name());
        return AuthenticationResponse.builder()
                .accessToken(accessToken).refreshToken(refreshToken)
                .tokenType("bearer")
                .expiresIn(jwtExpiration)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void logout(String userName) {
        val user = QueryChain.of(SystemUser.class).where(SYSTEM_USER.NAME.eq(userName)).one();
        revokeUserToken(user, Arrays.asList(TokenType.ACCESS_TOKEN, TokenType.REFRESH_TOKEN));
    }

    @Override
    public AuthenticationResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        val claimsJws = jwtProvider.validateToken(refreshToken);
        Claims body = claimsJws.getBody();
        val user = QueryChain.of(SystemUser.class).where(SYSTEM_USER.NAME.eq(body.get("username"))).one();
        return null;
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
