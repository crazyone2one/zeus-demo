package cn.master.zeus.dto;

import cn.master.zeus.entity.SystemGroup;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Created by 11's papa on 12/25/2023
 **/
@Setter
@Getter
public class GroupDTO extends SystemGroup {
    private String scopeName;
    private int memberSize;
}
