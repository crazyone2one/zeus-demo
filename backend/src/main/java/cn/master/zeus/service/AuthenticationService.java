package cn.master.zeus.service;

import cn.master.zeus.dto.request.AuthenticationRequest;
import cn.master.zeus.dto.response.AuthenticationResponse;

/**
 * @author Created by 11's papa on 12/12/2023
 **/
public interface AuthenticationService {
    /**
     * 登录接口
     *
     * @return cn.master.zeus.dto.response.AuthenticationResponse
     */
    AuthenticationResponse login(AuthenticationRequest request);
}

