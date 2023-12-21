import { IQueryMemberRequest } from '../interface'
import { IGroup, IUserGroup, IGroupResourceDto } from './group'
import alovaInstance from '/@/api/index'
export interface IUserItem {
  id: string
  name: string
  email: string
  lastProjectId: string
  lastWorkspaceId: string
  status: boolean
}
export interface IUserDto extends IUserItem {
  userGroups?: Array<IUserGroup>
  groups?: Array<IGroup>
  groupPermissions?: Array<IGroupResourceDto>
}

export const getProjectMemberPages = (workspaceId: string, param: IQueryMemberRequest) =>
  alovaInstance.Post<Array<IUserItem>>(`/user/ws/project/member/list/${workspaceId}`, param)
