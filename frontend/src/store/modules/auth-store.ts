import { defineStore } from 'pinia'
import { ILoginParams, loginAPI } from '/@/api/modules/auth'
interface IState {
  accessToken: string
  refreshToken: string
  userId: string
}
export const useAuthStore = defineStore('auth', {
  state: (): IState => {
    return {
      accessToken: '',
      refreshToken: '',
      userId: '',
    }
  },
  actions: {
    async login(param: ILoginParams) {
      loginAPI(param).then((res) => console.log(`output->res`, res))
    },
  },
  persist: true,
})
