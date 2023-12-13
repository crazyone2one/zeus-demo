package cn.master.zeus.common.exception;

import cn.master.zeus.config.CommonResult;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

/**
 * @author Created by 11's papa on 12/12/2023
 **/
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({UsernameNotFoundException.class})
    public ResponseEntity<Object> handleUsernameNotFoundException(UsernameNotFoundException e) {
        log.error("handle UsernameNotFoundException =>", e);
        //return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        return ResponseEntity.ok(CommonResult.error(HttpStatus.UNAUTHORIZED.value(), e.getMessage()));
    }

    @ExceptionHandler({BadCredentialsException.class})
    public ResponseEntity<Object> handleBadCredentialsException(BadCredentialsException e) {
        log.error("handle BadCredentialsException =>", e);
        return ResponseEntity.ok(CommonResult.error(HttpStatus.UNAUTHORIZED.value(), e.getMessage()));
    }

    @ExceptionHandler({ServiceException.class})
    public ResponseEntity<CommonResult<String>> handleServiceException(ServiceException e) {
        log.error("handle ServiceException =>", e);
        return ResponseEntity.ok(CommonResult.error(e));
        //return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<Object> handleException(MethodArgumentNotValidException e) {
        log.error("handle MethodArgumentNotValidException =>", e);
        val allErrors = e.getBindingResult().getAllErrors();
        String message = allErrors.stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(";"));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleException(Exception e) {
        log.error("handle exception {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
    }
}
