import { SelectOption } from 'naive-ui'

import { IPageResponse, IQueryParam } from '../interface'
import { IProjectItem } from './project'
import { IWorkspaceItem } from './workspace'
import alovaInstance from '/@/api/index'
export interface IUserGroup {
  id: string
  userId: string
  groupId: string
  sourceId: string
  userGroupPermissions?: Array<IUserGroupPermission>
  group?: IGroup
}
export interface IGroup {
  id: string
  name: string
  description: string
  system: boolean
  type: string
  scopeId: string
  global: boolean
  selects?: Array<string> // 选择项
  ids?: Array<string> // ids
  workspaceOptions?: Array<SelectOption> // 工作空间选项
  workspaces?: Array<IWorkspaceItem> // 工作空间选项
  projectOptions?: Array<SelectOption>
  projects?: Array<IProjectItem>
  showSearchGetMore?: boolean
}
export interface IGroupResource {
  id: string
  name: string
}
export interface IGroupPermission {
  id: string
  name: string
  resourceId: string
  checked: boolean
}
export interface IUserGroupPermission {
  id: string
  groupId: string
  permissionId: string
  moduleId: string
}
export interface IGroupResourceDto {
  group: IGroup
  userGroupPermissions: Array<IUserGroupPermission>
  type: string
  permissions: Array<IGroupPermission>
  resource: IGroupResource
}
interface IGroupPermissionDTO {
  permissions: Array<IGroupResourceDto>
}
export const getUserAllGroups = (userId: string) => alovaInstance.Get<Array<IGroup>>(`/user/group/all/${userId}`)
export const getAllUserGroupByType = (param: { type: string }) =>
  alovaInstance.Post<Array<IGroup>>(`/user/group/get`, param)
export const queryGroupPage = (page: number, pageSize: number, params: IQueryParam) => {
  params.pageNumber = page
  params.pageSize = pageSize
  return alovaInstance.Post<IPageResponse<IGroup>>('/user/group/page', params)
}
export const createUserGroup = (param: IGroup) => alovaInstance.Post<IGroup>('/user/group/save', param)
export const modifyUserGroup = (param: IGroup) => alovaInstance.Post<IGroup>('/user/group/save', param)
export const getUserGroupPermission = (param: IGroup) =>
  alovaInstance.Post<IGroupPermissionDTO>('/user/group/permission', param)
export const delUserGroupById = (ugId: string) => alovaInstance.Get(`/user/group/remove/${ugId}`)
