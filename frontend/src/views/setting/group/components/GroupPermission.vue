<script setup lang="ts">
import { IGroup, IGroupPermission } from '/@/api/modules/group'

interface IProp {
  permissions?: Array<IGroupPermission>
  selected?: Array<string>
  readOnly?: boolean
  group?: IGroup
}
const props = withDefaults(defineProps<IProp>(), {
  permissions: () => [],
  selected: () => [],
  readOnly: false,
  group: () => {
    return {} as IGroup
  },
})
const isReadOnly = (val: IGroupPermission) => {
  // 禁止取消系统管理员用户组权限
  if (props.group.id === 'super_group') {
    return true
  }
  // 禁止取消系统管理员用户组和超级管理员用户组的读取和设置权限
  const isSystemGroupPermission = val.id === 'SYSTEM_GROUP:READ' || val.id === 'SYSTEM_GROUP:READ+SETTING_PERMISSION'
  const isDefaultSystemGroup = props.group.id === 'admin' && isSystemGroupPermission
  return props.readOnly || isDefaultSystemGroup
}

const change = (val: boolean, permission: IGroupPermission) => {
  let id = permission.id.split(':')[1]
  if (id === 'READ' && !val) {
    props.permissions.map((p) => (p.checked = val))
  } else {
    let p = props.permissions.filter((p) => p.id.split(':')[1] === 'READ')
    if (p.length > 0) {
      p[0].checked = val
    }
    permission.checked = val
  }
}
</script>
<template>
  <n-space item-style="display: flex;" align="center">
    <n-checkbox
      v-for="(permission, index) in permissions"
      :key="index"
      v-model:checked="permission['checked']"
      :disabled="isReadOnly(permission)"
      @update:checked="change($event, permission)"
    >
      {{ $t(permission.name) }}
    </n-checkbox>
  </n-space>
</template>

<style scoped></style>
