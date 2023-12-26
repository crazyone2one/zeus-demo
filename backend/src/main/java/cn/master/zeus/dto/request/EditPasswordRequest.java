package cn.master.zeus.dto.request;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Created by 11's papa on 12/25/2023
 **/
@Getter
@Setter
public class EditPasswordRequest {
    private String password;
    private String newPassword;
    private String id;
}
