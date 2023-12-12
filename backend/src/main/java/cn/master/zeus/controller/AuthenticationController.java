package cn.master.zeus.controller;

import cn.master.zeus.dto.request.AuthenticationRequest;
import cn.master.zeus.dto.response.AuthenticationResponse;
import cn.master.zeus.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Created by 11's papa on 12/12/2023
 **/
@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    final AuthenticationService authenticationService;

    @PostMapping("/login")
    public AuthenticationResponse login(@RequestBody @Valid AuthenticationRequest request) {
        return authenticationService.login(request);
    }
}
