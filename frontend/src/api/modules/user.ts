import { IPageResponse, IQueryMemberRequest, IQueryParam } from '../interface'
import { IGroup, IGroupResourceDto, IUserGroup } from './group'
import alovaInstance from '/@/api/index'
export interface IUserItem {
  id: string
  name: string
  email: string
  phone: string
  lastProjectId: string
  lastWorkspaceId: string
  status: boolean
  password?: string
  groups?: Array<IGroup>
}
export interface IUserDto extends IUserItem {
  userGroups?: Array<IUserGroup>
  groups?: Array<IGroup>
  groupPermissions?: Array<IGroupResourceDto>
}

export const getProjectMemberPages = (workspaceId: string, param: IQueryMemberRequest) =>
  alovaInstance.Post<Array<IUserItem>>(`/user/ws/project/member/list/${workspaceId}`, param)

export const specialCreateUser = (user: IUserItem) => alovaInstance.Post<IUserItem>('/user/save', user)
export const specialListUsers = (page: number, pageSize: number, params: IQueryParam) => {
  params.pageNumber = page
  params.pageSize = pageSize
  return alovaInstance.Post<IPageResponse<IUserItem>>('/user/page', params)
}
