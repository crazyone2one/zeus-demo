import { IPageResponse, IQueryParam } from '../interface'
import { IUserDto } from './user'
import alovaInstance from '/@/api/index'
export interface IProjectItem {
  id: string
  name: string
  description: string
  workspaceId: string
  apiTemplateId?: string
  caseTemplateId?: string
  issueTemplateId?: string
  memberSize?: number
}
/**
 * 分页查询
 * @param page
 * @param pageSize
 * @param params
 * @returns
 */
export const queryProjectPage = (page: number, pageSize: number, params: IQueryParam) => {
  params.pageNumber = page
  params.pageSize = pageSize
  return alovaInstance.Post<IPageResponse<IProjectItem>>('/project/page', params)
}
/**
 * save project
 * @param params param
 * @returns
 */
export const saveProject = (params: IProjectItem) => {
  return alovaInstance.Post<IProjectItem>('/project/save', params)
}

export const getProjectList = () => alovaInstance.Get<IProjectItem[]>('/project/list')
export const getUserProjectList = (param: { userId: string; workspaceId: string }) =>
  alovaInstance.Post<Array<IProjectItem>>(`/project/list/related`, param)

export const switchProject = (param: { id: string; lastProjectId: string }) =>
  alovaInstance.Post<IUserDto>(`/user/update/current`, param)
