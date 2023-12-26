<script setup lang="ts">
import { ref } from 'vue'
import NModalDialog from '/@/components/NModalDialog.vue'
import { IGroup, createUserGroup } from '/@/api/modules/group'
import { useForm } from '@alova/scene-vue'
import { FormInst, FormRules, SelectOption } from 'naive-ui'
import { i18n } from '/@/i18n'
import { useRequest } from 'alova'
import { getWorkspaces } from '/@/api/modules/workspace'
import { getProjectList } from '/@/api/modules/project'

const modalDialog = ref<InstanceType<typeof NModalDialog> | null>(null)
const formRef = ref<FormInst | null>(null)
const {
  loading: submiting,
  send: submit,
  // 响应式的表单数据，内容由initialForm决定
  form: model,
} = useForm((model) => createUserGroup(model), {
  // 初始化表单数据
  initialForm: {
    id: '',
    name: '',
    description: '',
    global: false,
    system: false,
    type: '',
    groupCode: '',
    scopeId: '',
  },
  // 设置这个参数为true即可在提交完成后自动重置表单数据
  resetAfterSubmiting: true,
  immediate: false,
})
const rules: FormRules = {
  name: [
    { required: true, message: i18n.t('commons.input_name'), trigger: 'blur' },
    { min: 2, max: 50, message: i18n.t('commons.input_limit', [2, 50]), trigger: 'blur' },
  ],
  type: [{ required: true, message: i18n.t('group.select_type'), trigger: 'blur' }],
  scopeId: [{ required: true, message: i18n.t('group.select_belong_organization'), trigger: 'blur' }],
  description: { min: 2, max: 90, message: i18n.t('commons.input_limit', [2, 90]), trigger: 'blur' },
}
const title = ref(i18n.t('group.create'))
const dialogType = ref('')
const show = ref(true)
const isSystem = ref(false)
const showLabel = ref('')
const emit = defineEmits(['refresh'])
const handleSave = () => {
  formRef.value?.validate((err) => {
    if (!err) {
      console.log(`output->model.value`, model.value)
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
const open = (_type: string, _title: string, row?: IGroup): void => {
  title.value = _title
  dialogType.value = _type
  model.value = Object.assign({}, row || ({} as IGroup))
  workspaceOptions.value = []
  initWorkspace(row?.scopeId)
  modalDialog.value?.show()
}
const typeOptions = [
  {
    label: i18n.t('group.system'),
    value: 'SYSTEM',
  },
  {
    label: i18n.t('group.workspace'),
    value: 'WORKSPACE',
  },
  {
    label: i18n.t('group.project'),
    value: 'PROJECT',
  },
]
const workspaceOptions = ref<Array<SelectOption>>([])
const handleChange = (val: boolean) => (show.value = isSystem.value ? false : !val)
const { send: loadWs } = useRequest(() => getWorkspaces(), { immediate: false })
const { send: loadProject } = useRequest(() => getProjectList(), { immediate: false })
const initWorkspace = (scopeId: string | undefined) => {
  loadWs().then((res) => {
    let data = res
    if (data) {
      data.forEach((w) => {
        const option: SelectOption = {}
        option.label = w.name
        option.value = w.id
        workspaceOptions.value.push(option)
      })
      const workspace = workspaceOptions.value.find((w) => w.value === scopeId)
      if (workspace || !scopeId) {
        showLabel.value = i18n.t('project.owning_workspace')
      } else {
        showLabel.value = i18n.t('project.owning_project')
        loadProject().then((res) => {
          let data = res
          if (data) {
            data.forEach((w) => {
              const option: SelectOption = {}
              option.label = w.name
              option.value = w.id
              workspaceOptions.value.push(option)
            })
          }
        })
      }
    }
  })
}
defineExpose({ open })
</script>
<template>
  <n-spin :show="submiting">
    <n-modal-dialog ref="modalDialog" :title="title" @confirm="handleSave">
      <template #content>
        <n-form
          ref="formRef"
          :model="model"
          :rules="rules"
          label-placement="left"
          label-width="auto"
          require-mark-placement="right-hanging"
        >
          <n-form-item :label="$t('commons.name')" path="name">
            <n-input v-model:value="model.name" />
          </n-form-item>
          <n-form-item :label="$t('commons.encode')">
            <n-input v-model:value="model.groupCode" />
          </n-form-item>
          <n-form-item :label="$t('group.type')" path="type">
            <n-select v-model:value="model.type" :options="typeOptions" :disabled="dialogType === 'edit'" />
          </n-form-item>
          <n-form-item :label="$t('group.description')" path="description">
            <n-input v-model:value="model.description" type="textarea" />
          </n-form-item>
          <n-form-item :label="$t('group.global_group')">
            <n-switch
              v-model:value="model.global"
              :disabled="dialogType === 'edit' || model.type === 'SYSTEM'"
              @update:value="handleChange"
            />
          </n-form-item>
          <n-form-item v-if="show" :label="showLabel" path="scopeId">
            <n-select
              v-model:value="model.scopeId"
              :options="workspaceOptions"
              :disabled="dialogType === 'edit'"
              filterable
              clearable
            />
          </n-form-item>
        </n-form>
      </template>
    </n-modal-dialog>
  </n-spin>
</template>

<style scoped></style>
