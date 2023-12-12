package cn.master.zeus.auth;

import cn.master.zeus.dto.CustomUserDetails;
import cn.master.zeus.entity.SystemToken;
import com.mybatisflex.core.query.QueryChain;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

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
        List<SimpleGrantedAuthority> userDetailsAuthorities = (List<SimpleGrantedAuthority>) userDetails.getAuthorities();
        String authorities = userDetailsAuthorities.stream().map(SimpleGrantedAuthority::getAuthority)
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
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
    }

    private String buildJwtToken(Map<String, Object> body, long expiration) {

        Date tokenCreateTime = new Date(System.currentTimeMillis());
        Date tokenValidity = new Date(tokenCreateTime.getTime() + expiration);
        try {
            return Jwts.builder()
                    .addClaims(body)
                    .setIssuer("zeus")
                    .setSubject((String) body.get("username"))
                    .setIssuedAt(tokenCreateTime)
                    .setExpiration(tokenValidity)
                    .signWith(SignatureAlgorithm.HS256, secretKey)
                    .compact();
        } catch (JwtException e) {
            throw new JwtException(e.getMessage());
        }
    }
}
