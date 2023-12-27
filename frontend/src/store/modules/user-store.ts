import { defineStore } from 'pinia'
import { ref } from 'vue'
import { IUserDto } from '/@/api/modules/user'

// You can name the return value of `defineStore()` anything you want,
// but it's best to use the name of the store and surround it with `use`
// and `Store` (e.g. `useUserStore`, `useCartStore`, `useProductStore`)
// the first argument is a unique id of the store across your application
export const useUserStore = defineStore(
  'user',
  () => {
    const user = ref<IUserDto>({} as IUserDto)
    const saveSessionStorage = (user: IUserDto): void => {
      // 校验权限
      user.userGroups?.forEach((ug) => {
        user.groupPermissions?.forEach((gp) => {
          if (gp.group.id === ug.groupId) {
            ug.userGroupPermissions = gp.userGroupPermissions
            ug.group = gp.group
          }
        })
      })
      // 检查当前项目有没有权限
      const currentProjectId = sessionStorage.getItem('project_id')
      if (!currentProjectId) {
        sessionStorage.setItem('project_id', user.lastProjectId)
      } else {
        const v = user.userGroups
          ?.filter((ug) => ug.group && ug.group.type === 'PROJECT')
          .filter((ug) => ug.sourceId === currentProjectId)
        const index = user.groups?.findIndex((g) => g.id === 'super_group')
        if (v?.length === 0 && index === -1) {
          sessionStorage.setItem('project_id', user.lastProjectId)
        }
      }
      if (!sessionStorage.getItem('workspace_id')) {
        sessionStorage.setItem('workspace_id', user.lastWorkspaceId)
      }
    }
    const clearSessionStorage = (): void => {
      sessionStorage.removeItem('workspace_id')
      sessionStorage.removeItem('project_id')
      sessionStorage.removeItem('workspace_name')
      sessionStorage.removeItem('project_name')
    }
    const initUser = () => {
      user.value = {} as IUserDto
      clearSessionStorage()
    }
    return { user, saveSessionStorage, initUser }
  },
  { persist: true },
)
