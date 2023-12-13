package cn.master.zeus.auth;

import cn.master.zeus.dto.CustomUserDetails;
import cn.master.zeus.entity.SystemToken;
import com.mybatisflex.core.query.QueryChain;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

import static cn.master.zeus.entity.table.SystemTokenTableDef.SYSTEM_TOKEN;

/**
 * @author Created by 11's papa on 12/10/2023
 **/
@Slf4j
@Component
public class JwtProvider {

    @Value("${security.jwt.secret-key}")
    private String secretKey;
    @Value("${security.jwt.accessToken.expiration:86400000}")
    private long jwtExpiration;
    @Value("${security.jwt.refreshToken.expiration:604800000}")
    private long refreshExpiration;

    public String generateAccessToken(CustomUserDetails userDetails) {
        Collection<? extends GrantedAuthority> userDetailsAuthorities = userDetails.getAuthorities();
        String authorities = userDetailsAuthorities.stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        Map<String, Object> tokenBody = new LinkedHashMap<>();
        tokenBody.put("username", userDetails.getUsername());
        tokenBody.put("authorities", authorities);
        return buildJwtToken(tokenBody, jwtExpiration);
    }

    public String generateRefreshToken(CustomUserDetails userDetails) {
        Map<String, Object> tokenBody = new LinkedHashMap<>();
        tokenBody.put("username", userDetails.getUsername());
        return buildJwtToken(tokenBody, refreshExpiration);
    }

    public Jws<Claims> validateToken(String token) {
        val systemToken = QueryChain.of(SystemToken.class)
                .where(SYSTEM_TOKEN.TOKEN.eq(token).and(SYSTEM_TOKEN.REVOKED.eq(false))).one();
        if (Objects.isNull(systemToken)) {
            throw new JwtException("Invalid token");
        }
        return Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token);
    }

    /**
     * 生成token
     *
     * @param claims     claims
     * @param expiration token过期时间
     * @return java.lang.String
     */
    private String buildJwtToken(Map<String, Object> claims, long expiration) {
        Date tokenCreateTime = new Date(System.currentTimeMillis());
        Date tokenValidity = new Date(tokenCreateTime.getTime() + expiration);
        try {
            return Jwts.builder()
                    .setClaims(claims)
                    .setIssuer("zeus")
                    .setSubject((String) claims.get("username"))
                    .setIssuedAt(tokenCreateTime)
                    .setExpiration(tokenValidity)
                    .signWith(getSignKey(), SignatureAlgorithm.HS256)
                    .compact();
        } catch (JwtException e) {
            throw new JwtException(e.getMessage());
        }
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
