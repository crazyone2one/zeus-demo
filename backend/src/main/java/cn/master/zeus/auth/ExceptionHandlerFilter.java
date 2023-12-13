package cn.master.zeus.auth;

import cn.master.zeus.config.CommonResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * @author Created by 11's papa on 12/13/2023
 **/
@Component
public class ExceptionHandlerFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (RuntimeException e) {
            // custom error response class
            CommonResult<String> apiError = new CommonResult<>();
            apiError.setMessage(e.getMessage());

            int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
            if (e instanceof JwtException) {
                status = HttpStatus.UNAUTHORIZED.value();
            }
            response.setStatus(status);
            response.getWriter().write(convertObjectToJson(apiError));
        }
    }

    private String convertObjectToJson(Object object) throws JsonProcessingException {
        if (object == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }
}
