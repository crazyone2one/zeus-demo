package cn.master.zeus.dto.request.member;

import cn.master.zeus.entity.SystemUser;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Created by 11's papa on 12/22/2023
 **/
@Setter
@Getter
public class UserRequest extends SystemUser {
    private List<Map<String, Object>> groups = new ArrayList<>();
}
