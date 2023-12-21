import { SelectOption } from 'naive-ui'
import { IPageResponse, IQueryParam } from '../interface'
import alovaInstance from '/@/api/index'
import { IUserDto } from './user'
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

export const getUserWorkspaceList = () => {
  // if (transfor) {
  //   return alovaInstance.Get<Array<SelectOption>>(`/workspace/list/user/workspace`, {
  //     transformData(data) {
  //       return list2SelectOption(data as Array<IWorkspaceItem>)
  //     },
  //   })
  // }
  return alovaInstance.Get<Array<IWorkspaceItem>>('/workspace/list/user/workspace')
}

export const createWs = (params: IWorkspaceItem) => {
  return alovaInstance.Post<IWorkspaceItem>('/workspace/special/add', params)
}

const updateWs = (params: any) => {
  return alovaInstance.Put(`/workspace/${params.id}`, params)
}
export const switchWorkspace = (wsId: string) => alovaInstance.Get<IUserDto>(`/user/switch/source/ws/${wsId}`)
const list2SelectOption = (val: Array<IWorkspaceItem>) => {
  const result: SelectOption[] = []
  val.forEach((item) => {
    const option: SelectOption = {}
    option.label = item.name
    option.value = item.id
    result.push(option)
  })
  return result
}
