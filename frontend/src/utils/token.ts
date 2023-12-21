import { IUserDto } from '../api/modules/user'
import { useUserStore } from '/@/store/modules/user-store'
/**
 * get user info from store
 * @returns user info
 */
export const getCurrentUser = (): IUserDto => {
  const store = useUserStore()
  return store.user
}
export const getCurrentUserId = (): string => {
  return getCurrentUser().id
}

export const getCurrentWorkspaceId = (): string => {
  const workspaceId = sessionStorage.getItem('workspace_id')
  if (workspaceId) {
    return workspaceId
  }
  return getCurrentUser().lastWorkspaceId
}

export const getCurrentProjectId = (): string => {
  const projectId = sessionStorage.getItem('project_id')
  if (projectId) {
    return projectId
  }
  return getCurrentUser().lastProjectId
}
