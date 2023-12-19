import { IPageResponse, IQueryParam } from '../interface'
import alovaInstance from '/@/api/index'
export interface IWorkspaceItem {
  id: string
  name: string
  description: string
  memberSize?: number
}

export const queryWsPage = (page: number, pageSize: number, params: IQueryParam) => {
  params.pageNumber = page
  params.pageSize = pageSize
  return alovaInstance.Post<IPageResponse<IWorkspaceItem>>('/workspace/page', params)
}

const getWsById = (id: string) => {
  return alovaInstance.Get(`/workspace/${id}`)
}

export const createWs = (params: IWorkspaceItem) => {
  return alovaInstance.Post<IWorkspaceItem>('/workspace/special/add', params)
}

const updateWs = (params: any) => {
  return alovaInstance.Put(`/workspace/${params.id}`, params)
}
