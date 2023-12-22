package cn.master.zeus.auth;

import cn.master.zeus.common.exception.ServiceException;
import cn.master.zeus.service.UserDetailsServiceImpl;
import io.jsonwebtoken.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    final JwtProvider jwtProvider;
    final UserDetailsServiceImpl userDetailsService;

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
            // 将认证成功的用户保存到上下文
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    userDetails.getPassword(),
                    simpleGrantedAuthorities
            );
            log.info("authenticated user with email/username :{}", username);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (UnsupportedJwtException exception) {
            throw new ServiceException(HttpStatus.UNAUTHORIZED.value(), "Unsupported JWT token");
        } catch (MalformedJwtException exception) {
            throw new ServiceException(HttpStatus.UNAUTHORIZED.value(), "Invalid JWT token");
        } catch (SecurityException exception) {
            throw new ServiceException(HttpStatus.UNAUTHORIZED.value(), "Invalid format token");
        } catch (JwtException e) {
            throw new JwtException(e.getMessage());
        }
        filterChain.doFilter(request, response);
    }
}
