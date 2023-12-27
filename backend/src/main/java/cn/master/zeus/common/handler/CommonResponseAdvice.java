package cn.master.zeus.common.handler;

import cn.master.zeus.common.annotation.NoResultHolder;
import cn.master.zeus.common.handler.ResultHolder;
import cn.master.zeus.config.IgnoreResponseAdvice;
import cn.master.zeus.util.JsonUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Objects;

/**
 * @author Created by 11's papa on 12/12/2023
 **/
@RestControllerAdvice
public class CommonResponseAdvice implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 若为以下情况，则不进行响应体封装
        // 1. 该注解存在在方法上
        // 2. 该注解存在在类上
        // 3. 返回体已经是ResponseResult类型

        return !(returnType.getContainingClass().isAnnotationPresent(IgnoreResponseAdvice.class) ||
                Objects.requireNonNull(returnType.getMethod()).isAnnotationPresent(IgnoreResponseAdvice.class)) ||
                MappingJackson2HttpMessageConverter.class.isAssignableFrom(converterType) ||
                StringHttpMessageConverter.class.isAssignableFrom(converterType);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> converterType, ServerHttpRequest request, ServerHttpResponse response) {

        // 处理空值
        if (body == null && StringHttpMessageConverter.class.isAssignableFrom(converterType)) {
            return null;
        }
        if (methodParameter.hasMethodAnnotation(NoResultHolder.class)) {
            return body;
        }
        if (!(body instanceof ResultHolder)) {
            // String类型需要特别处理，否则会发生类型转换异常
            if (body instanceof String) {
                // 设置响应头
                response.getHeaders().add("Content-Type", "application/json");
                return JsonUtils.toJsonPrettyString(ResultHolder.success(body));
            }
            return ResultHolder.success(body);
        }
        return body;
    }
}
