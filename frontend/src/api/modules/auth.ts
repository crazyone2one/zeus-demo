import { IUserDto } from './user'
import alovaInstance from '/@/api/index'

export interface ILoginParams {
  username: string
  password: string
}
interface ILoginRespItem {
  accessToken: string
  refreshToken: string
  userId: string
  user: IUserDto
}
/**
 * 登录方法
 * @param params 登录参数
 * @returns
 */
export const loginAPI = (params: ILoginParams) => {
  const methodInstance = alovaInstance.Post<ILoginRespItem>('/api/v1/auth/login', params)
  methodInstance.meta = {
    ignoreToken: true,
  }
  return methodInstance
}
export const logoutAPI = () => alovaInstance.Post('/api/v1/auth/logout')
