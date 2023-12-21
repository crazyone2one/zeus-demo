import { IPageResponse, IQueryParam } from '../interface'
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