package cn.master.zeus.dto;

import cn.master.zeus.entity.Project;
import cn.master.zeus.entity.Workspace;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by 11's papa on 12/22/2023
 **/
@Data
public class WorkspaceResource {
    private List<Project> projects = new ArrayList<>();
    private List<Workspace> workspaces = new ArrayList<>();
}
