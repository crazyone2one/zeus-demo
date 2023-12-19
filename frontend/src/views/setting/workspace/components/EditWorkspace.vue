<script setup lang="ts">
import { ref } from 'vue'
import NModalDialog from '/@/components/NModalDialog.vue'
import { createWs } from '/@/api/modules/workspace'
import { useForm } from '@alova/scene-vue'
import { FormInst, FormRules } from 'naive-ui'
import { i18n } from '/@/i18n'

const modalDialog = ref<InstanceType<typeof NModalDialog> | null>(null)
const formRef = ref<FormInst | null>(null)
const {
  loading: submiting,
  send: submit,
  // 响应式的表单数据，内容由initialForm决定
  form: model,
} = useForm((model) => createWs(model), {
  // 初始化表单数据
  initialForm: {
    id: '',
    name: '',
    description: '',
  },
  // 设置这个参数为true即可在提交完成后自动重置表单数据
  resetAfterSubmiting: true,
  immediate: false,
})
const rules: FormRules = {
  name: [{ required: true, message: i18n.t('workspace.input_name'), trigger: 'blur' }],
  description: {
    max: 50,
    message: i18n.t('commons.input_limit', [0, 50]),
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
const open = (): void => {
  modalDialog.value?.show()
}
defineExpose({ open })
</script>
<template>
  <n-spin :show="submiting">
    <n-modal-dialog ref="modalDialog" :title="$t('workspace.create')" @confirm="handleSave">
      <template #content>
        <n-form
          ref="formRef"
          :model="model"
          :rules="rules"
          label-placement="left"
          require-mark-placement="right-hanging"
        >
          <n-form-item :label="$t('commons.name')" path="name">
            <n-input v-model:value="model.name" />
          </n-form-item>
          <n-form-item :label="$t('commons.description')" path="description">
            <n-input v-model:value="model.description" type="textarea" />
          </n-form-item>
        </n-form>
      </template>
    </n-modal-dialog>
  </n-spin>
</template>

<style scoped></style>
