package cn.master.zeus.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author Created by 11's papa on 12/25/2023
 **/
@Data
public class GroupJson implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private List<GroupResource> resource;
    private List<GroupPermission> permissions;
}
