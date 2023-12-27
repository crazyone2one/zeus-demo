package cn.master.zeus.error;

import cn.master.zeus.common.handler.ResultHolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

/**
 * @author Created by 11's papa on 12/10/2023
 **/

@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    final HttpMessageConverter<String> messageConverter;
    final ObjectMapper mapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        ResultHolder apiError = new ResultHolder();
        apiError.setMessage(e.getMessage());

        ServerHttpResponse outputMessage = new ServletServerHttpResponse(response);
        outputMessage.setStatusCode(HttpStatus.UNAUTHORIZED);

        messageConverter.write(mapper.writeValueAsString(apiError), MediaType.APPLICATION_JSON, outputMessage);
    }
}
