package cn.master.zeus.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MissingClaimException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Created by 11's papa on 12/10/2023
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasLength(authorizationHeader) || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = authorizationHeader.replace("Bearer ", "");
        try {
            val claimsJws = jwtProvider.validateToken(token);
            Claims body = claimsJws.getBody();
            String username = (String) body.get("username");
            String authorities;
            if (body.get("authorities") == null) {
                throw new MissingClaimException(claimsJws.getHeader(), body, "Token is invalid");
            } else {
                authorities = (String) body.get("authorities");
            }
            Set<SimpleGrantedAuthority> simpleGrantedAuthorities = new HashSet<>();

            Arrays.asList(authorities.split(" "))
                    .forEach(a -> simpleGrantedAuthorities.add(new SimpleGrantedAuthority(a)));
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    simpleGrantedAuthorities
            );
            log.info("authenticated user with email :{}", username);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (ExpiredJwtException e) {
            throw new JwtException("Token is expired");
        } catch (JwtException e) {
            throw new JwtException(e.getMessage());
        }
        filterChain.doFilter(request, response);
    }
}
