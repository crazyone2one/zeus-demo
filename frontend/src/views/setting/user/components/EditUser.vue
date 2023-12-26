<script setup lang="ts">
import { useForm } from '@alova/scene-vue'
import { useRequest } from 'alova'
import { FormInst, FormRules, SelectOption } from 'naive-ui'
import { ref } from 'vue'
import { IGroup, getAllUserGroupByType, getUserAllGroups } from '/@/api/modules/group'
import { IProjectItem } from '/@/api/modules/project'
import { IUserItem, specialCreateUser, specialModifyUser } from '/@/api/modules/user'
import { IWorkspaceItem } from '/@/api/modules/workspace'
import NModalDialog from '/@/components/NModalDialog.vue'
import { i18n } from '/@/i18n'
import { GROUP_TYPE } from '/@/utils/constants'

const modalDialog = ref<InstanceType<typeof NModalDialog> | null>(null)
const formRef = ref<FormInst | null>(null)
const type = ref<string>('Add')
const title = ref('创建用户')
const userGroup = ref<Array<IGroup>>([])
const limitOptionCount = ref(400)
const btnAddGroup = ref(false)
const currentGroupWSIds = ref(new Set<string>())
const currentWSGroupIndex = ref(-1)
const {
  loading: submiting,
  send: submit,
  // 响应式的表单数据，内容由initialForm决定
  form: model,
} = useForm((model) => (type.value === 'Add' ? specialCreateUser(model) : specialModifyUser(model)), {
  // 初始化表单数据
  initialForm: {
    id: '',
    name: '',
    email: '',
    status: true,
    phone: '',
    password: '',
    groups: [] as Array<IGroup>,
  } as IUserItem,
  // 设置这个参数为true即可在提交完成后自动重置表单数据
  resetAfterSubmiting: true,
  immediate: false,
})
const rules: FormRules = {
  name: [
    { required: true, message: i18n.t('user.input_name'), trigger: 'blur' },
    {
      min: 2,
      max: 50,
      message: i18n.t('commons.input_limit', [2, 50]),
      trigger: 'blur',
    },
  ],
  email: {
    required: true,
    message: i18n.t('user.input_email', [0, 50]),
    trigger: 'blur',
  },
}
const emit = defineEmits(['refresh'])
const handleSave = () => {
  formRef.value?.validate((err) => {
    if (!err) {
      submit()
        .then(() => {
          modalDialog.value?.hideModal()
          emit('refresh')
          window.$message.success(i18n.t('commons.save_success'))
        })
        .catch((e) => window.$message.error(e.message))
    } else {
      return
    }
  })
}
const { send: loadUserGroup } = useRequest((val) => getUserAllGroups(val), { immediate: false })
const { send: loadUserGroupByType } = useRequest((val) => getAllUserGroupByType(val), { immediate: false })
const open = (_type?: string, _title?: string, row?: IUserItem): void => {
  title.value = _title || title.value
  type.value = _type || type.value
  if (_type === 'Edit') {
    submiting.value = true
    loadUserGroup(row?.id).then((res) => {
      model.value.groups = res
      model.value.groups.forEach((item) => {
        handleWorkspaceOption(item, item.workspaces)
        handleProjectOption(item, item.projects)
      })
    })
    model.value = Object.assign({}, row)
  }
  loadUserGroupByType({ type: GROUP_TYPE.SYSTEM }).then((res) => (userGroup.value = res))
  modalDialog.value?.show()
  submiting.value = false
}
const handleWorkspaceOption = (group: IGroup, workspaces: Array<IWorkspaceItem> | undefined): void => {
  if (!workspaces) {
    return
  }
  group.showSearchGetMore = workspaces.length > limitOptionCount.value
  const tmp = workspaces.slice(0, limitOptionCount.value)
  const options: Array<SelectOption> = []
  tmp.forEach((w) => {
    const option: SelectOption = {}
    option.label = w.name
    option.value = w.id
    options.push(option)
  })
  group.workspaceOptions = options
  if (!group.ids || group.ids.length === 0) {
    return
  }
  group.ids.forEach((id) => {
    let index = options.findIndex((o) => o.value === id)
    if (index <= -1) {
      let obj = workspaces.find((d) => d.id === id)
      if (obj) {
        const _obj: SelectOption = { value: obj.id, label: obj.name }
        group.workspaceOptions?.unshift(_obj)
      }
    }
  })
}
const handleProjectOption = (group: IGroup, projects: Array<IProjectItem> | undefined): void => {
  if (!projects) {
    return
  }
  group.showSearchGetMore = projects.length > limitOptionCount.value
  const tmp = projects.slice(0, limitOptionCount.value)
  const options: Array<SelectOption> = []
  tmp.forEach((w) => {
    const option: SelectOption = {}
    option.label = w.name
    option.value = w.id
    options.push(option)
  })
  group.projectOptions = options
  if (!group.ids || group.ids.length === 0) {
    return
  }
  group.ids.forEach((id) => {
    let index = options.findIndex((o) => o.value === id)
    if (index <= -1) {
      let obj = projects.find((d) => d.id === id)
      if (obj) {
        const _obj: SelectOption = { value: obj.id, label: obj.name }
        group.projectOptions?.unshift(_obj)
      }
    }
  })
}
const addGroup = (v: FormInst | null) => {
  v?.validate((error) => {
    if (!error) {
      let roleInfo = {}
      roleInfo.selects = []
      const ids = model.value.groups.map((r) => r.type)
      ids.forEach((id) => {
        roleInfo.selects.push(id)
      })
      let groups = model.value.groups
      groups.push(roleInfo)
      if (model.value.groups.length > userGroup.value.length - 1) {
        btnAddGroup.value = true
      }
    } else {
      return false
    }
  })
}
const getLabel = (index: number): string => {
  let a = index + 1
  return i18n.t('commons.group') + a
}
const activeGroup = (group: IGroup): Array<SelectOption> => {
  const tmp = userGroup.value.filter((ug) => {
    if (!group.selects) {
      return true
    }
    let sign = true
    for (let groupSelect of group.selects) {
      if (ug.id + '+' + ug.type === groupSelect) {
        sign = false
        break
      }
    }
    return sign
  })
  const options: Array<SelectOption> = []
  if (tmp.length > 0) {
    tmp.forEach((i) => {
      const option: SelectOption = {}
      option.label = i.name
      option.value = i.id
      options.push(option)
    })
  }
  return options
}
const groupType = (idType: string) => {
  if (!idType) {
    return
  }
  return idType.split('+')[1]
}
const handleUpdateWorkSpace = (index: number, type: string) => {
  let _type = type.split('+')[1]
  if (_type === GROUP_TYPE.WORKSPACE) {
    currentGroupWSIds.value.forEach((item) => {
      if (model.value.groups[index] && model.value.groups[index].ids) {
        model.value.groups[index].ids?.push(item)
      }
    })
  } else {
    model.value.groups[index].ids = []
  }
}
const setWorkSpaceIds = (ids: Array<string>, projects: Array<IProjectItem>) => {
  projects.forEach((project) => {
    ids.forEach((item) => {
      if (item === project.id) {
        currentGroupWSIds.value.add(project.workspaceId)
        if (
          model.value.groups[currentWSGroupIndex.value] &&
          model.value.groups[currentWSGroupIndex.value].ids?.indexOf(project.workspaceId) === -1
        ) {
          model.value.groups[currentWSGroupIndex.value].ids?.push(project.workspaceId)
        } else if (
          model.value.groups.filter((g) => g?.type === 'ws_member+WORKSPACE').length > 0 &&
          model.value.groups.filter((g) => g?.type === 'ws_member+WORKSPACE')[0].ids &&
          model.value.groups.filter((g) => g?.type === 'ws_member+WORKSPACE')[0].ids?.indexOf(project.workspaceId) ===
            -1
        ) {
          model.value.groups.filter((g) => g?.type === 'ws_member+WORKSPACE')[0].ids?.push(project.workspaceId)
        }
      }
    })
  })
}
defineExpose({ open })
</script>
<template>
  <n-spin :show="submiting">
    <n-modal-dialog ref="modalDialog" :title="title" class="width: 80%" @confirm="handleSave">
      <template #content>
        <n-form
          ref="formRef"
          :model="model"
          :rules="rules"
          label-placement="left"
          require-mark-placement="right-hanging"
          label-width="auto"
        >
          <n-form-item :label="$t('commons.name')" path="name">
            <n-input v-model:value="model.name" :placeholder="$t('user.input_name')" />
          </n-form-item>
          <n-form-item :label="$t('commons.email')" path="email">
            <n-input v-model:value="model.email" :placeholder="$t('user.input_email')" />
          </n-form-item>
          <n-form-item :label="$t('commons.phone')" path="phone">
            <n-input v-model:value="model.phone" :placeholder="$t('user.input_phone')" />
          </n-form-item>
          <n-form-item v-if="type === 'Add'" :label="$t('commons.password')" path="password">
            <n-input v-model:value="model.password" :placeholder="$t('user.input_password')" />
          </n-form-item>
          <div v-for="(group, index) in model.groups" :key="index">
            <n-form-item :label="getLabel(index)" :path="'groups.' + index + '.type'">
              <n-select
                v-model:value="group.type"
                :options="activeGroup(group)"
                :placeholder="$t('user.select_group')"
                :disabled="model.groups[index].type != null && model.groups[index].type !== ''"
              />
              <n-button style="margin-left: 20px"> {{ $t('commons.delete') }}</n-button>
            </n-form-item>
            <div v-if="groupType(group.type) === GROUP_TYPE.WORKSPACE">
              <n-form-item :label="$t('commons.workspace')" :path="'groups.' + index + '.type'">
                <n-select
                  v-model:value="group.ids"
                  filterable
                  multiple
                  :options="group.workspaceOptions"
                  :placeholder="$t('system_user.search_get_more_tip')"
                  @update:value="handleUpdateWorkSpace(group.index, group.type)"
                />
              </n-form-item>
            </div>
            <div v-if="groupType(group.type) === GROUP_TYPE.PROJECT">
              <n-form-item :label="$t('commons.project')" :path="'groups.' + index + '.type'">
                <n-select
                  v-model:value="group.ids"
                  filterable
                  multiple
                  :options="group.projectOptions"
                  :placeholder="$t('system_user.search_get_more_tip')"
                  @update:value="setWorkSpaceIds(group.ids, group.projects)"
                />
              </n-form-item>
            </div>
          </div>
          <n-form-item>
            <n-button type="success" :disabled="btnAddGroup" @click="addGroup(formRef)">{{ $t('group.add') }}</n-button>
          </n-form-item>
        </n-form>
      </template>
    </n-modal-dialog>
  </n-spin>
</template>

<style scoped></style>
