<script setup lang="ts">
import { i18n } from '../i18n'
import NTableOperatorButton from './NTableOperatorButton.vue'
interface IProp {
  showEdit?: boolean
  showDelete?: boolean
  isShow?: boolean
  tip1?: string
  tip2?: string
  editPermission?: Array<string>
  deletePermission?: Array<string>
}
withDefaults(defineProps<IProp>(), {
  showEdit: true,
  showDelete: true,
  isShow: false,
  tip1: () => i18n.t('commons.edit'),
  tip2: () => i18n.t('commons.delete'),
  editPermission: () => [],
  deletePermission: () => [],
})
const emits = defineEmits(['deleteClick', 'editClick'])
const editClick = () => emits('editClick')
const deleteClick = () => emits('deleteClick')
</script>
<template>
  <n-space>
    <slot name="front"></slot>
    <div v-permission="editPermission">
      <n-table-operator-button
        v-if="showEdit"
        :tip="tip1"
        icon="i-mdi:circle-edit-outline"
        :disabled="isShow"
        @exec="editClick"
      />
    </div>

    <slot name="middle"></slot>
    <div v-permission="deletePermission">
      <n-table-operator-button
        v-if="showDelete"
        :tip="tip2"
        icon="i-mdi:delete"
        type="error"
        :disabled="isShow"
        @exec="deleteClick"
      />
    </div>

    <slot name="behind"></slot>
  </n-space>
</template>

<style scoped></style>
