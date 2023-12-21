package cn.master.zeus.dto.response;

import cn.master.zeus.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Created by 11's papa on 12/12/2023
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    private String accessToken;
    private String tokenType;
    private Integer expiresIn;
    private String refreshToken;
    private String userId;
    private UserDTO user;
}
