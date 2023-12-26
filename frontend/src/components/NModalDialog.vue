<script setup lang="ts">
import { NModal } from 'naive-ui'
import { ref } from 'vue'
import { i18n } from '/@/i18n'

interface Props {
  title?: string
  showFooter?: boolean // 是否展示底部操作按钮
  btnText?: string
  dialogWidth?: string
}
withDefaults(defineProps<Props>(), {
  title: i18n.t('commons.operating'), // modal title
  showFooter: true, // show footer button
  btnText: '',
  dialogWidth: '446px',
})
const showModal = ref(false)
const emits = defineEmits(['confirm', 'cancel', 'saveAsEdit', 'closeModal'])
const onCancel = (): void => {
  showModal.value = false
  emits('cancel')
}
const toggleModal = () => {
  showModal.value = !showModal.value
  return Promise.resolve(showModal.value)
}
// * show modal
const show = () => {
  showModal.value = true
  return Promise.resolve(true)
}
const hideModal = () => {
  showModal.value = false
  return Promise.resolve(false)
}
defineExpose({ toggleModal, show, hideModal })
</script>
<template>
  <div>
    <n-modal
      v-model:show="showModal"
      :mask-closable="false"
      :show-icon="false"
      preset="dialog"
      :style="{ width: dialogWidth }"
      :title="title"
    >
      <slot name="content" />
      <template #action>
        <n-space v-if="showFooter" justify="end">
          <n-button type="default" size="small" @click="onCancel">
            {{ $t('commons.cancel') }}
          </n-button>
          <n-button type="primary" size="small" @click="emits('confirm')">
            {{ $t('commons.confirm') }}
          </n-button>
          <n-button v-if="btnText" type="primary" size="small" @click="emits('saveAsEdit')">
            {{ btnText }}
          </n-button>
        </n-space>
      </template>
    </n-modal>
  </div>
</template>

<style scoped></style>
