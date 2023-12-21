package cn.master.zeus.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Created by 11's papa on 12/20/2023
 **/
@Data
public class GroupResource implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private Boolean license = false;

    /**
     * 系统设置、工作空间、项目类型 公用的权限模块
     * e.g. 个人信息
     */
    private boolean global = false;
}
