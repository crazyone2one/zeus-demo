package cn.master.zeus.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by 11's papa on 12/25/2023
 **/
@Data
public class GroupPermissionDTO {
    private List<GroupResourceDTO> permissions = new ArrayList<>();
}
