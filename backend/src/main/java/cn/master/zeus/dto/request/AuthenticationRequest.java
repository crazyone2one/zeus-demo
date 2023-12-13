package cn.master.zeus.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Created by 11's papa on 12/11/2023
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {
    @NotBlank(message = "userName or email is required")
    private String username;
    @NotBlank(message = "Password is required")
    private String password;
}
