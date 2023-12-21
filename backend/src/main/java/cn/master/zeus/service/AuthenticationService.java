package cn.master.zeus.service;

import cn.master.zeus.dto.UserDTO;
import cn.master.zeus.dto.request.AuthenticationRequest;
import cn.master.zeus.dto.request.RefreshTokenRequest;
import cn.master.zeus.dto.response.AuthenticationResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Created by 11's papa on 12/12/2023
 **/
public interface AuthenticationService {
    /**
     * 登录接口
     *
     * @return cn.master.zeus.dto.response.AuthenticationResponse
     */
    AuthenticationResponse login(AuthenticationRequest request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);

    void logout(String userName);
    /**
     * refresh token
     *
     * @param request param
     * @return cn.master.zeus.dto.response.AuthenticationResponse
     */
    AuthenticationResponse refreshToken(RefreshTokenRequest request);

    UserDTO getUserInfo(String userId);
}

