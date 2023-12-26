package cn.master.zeus.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Created by 11's papa on 12/22/2023
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class UserRequest extends BaseRequest {
    private String email;
}
