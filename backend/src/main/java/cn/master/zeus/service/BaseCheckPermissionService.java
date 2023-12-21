package cn.master.zeus.service;

/**
 * @author Created by 11's papa on 12/21/2023
 **/
public interface BaseCheckPermissionService {
    void checkProjectBelongToWorkspace(String projectId, String workspaceId);
}
