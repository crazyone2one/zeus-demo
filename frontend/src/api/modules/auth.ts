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
    authRole: 'login',
  }
  return methodInstance
}
/**
 * 退出方法
 * @returns 退出方法实例
 */
export const logoutAPI = () => {
  const method = alovaInstance.Post('/api/v1/auth/logout')
  method.meta = {
    authRole: 'logout',
  }
  return method
}
/**
 * 刷新token方法
 * @param param token信息
 * @returns 刷新token实例
 */
export const refreshUserToken = (param: { refreshToken: string }) => {
  const method = alovaInstance.Post<ILoginRespItem>('/api/v1/auth/refresh', param)
  method.meta = {
    authRole: 'refreshToken',
    ignoreToken: true,
  }
  return method
}
