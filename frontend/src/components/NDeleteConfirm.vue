<script setup lang="ts">
import { ref } from 'vue'
import { i18n } from '../i18n'
import NModalDialog from '/@/components/NModalDialog.vue'
type recordKey = 'id' | 'name'
interface IProp {
  withTip?: boolean
  title?: string
}
withDefaults(defineProps<IProp>(), {
  title: () => i18n.t('commons.title'),
  withTip: false,
})
const emit = defineEmits(['delete'])
const modalDialog = ref<InstanceType<typeof NModalDialog> | null>(null)
const value = ref('')
const record = ref<Record<recordKey, string>>({ id: '', name: '' })
const confirm = () => {
  if (value.value.trim() !== ('DELETE-' + record.value.name).trim()) {
    window.$message.warning(i18n.t('commons.incorrect_input'))
    return
  }
  emit('delete', record.value)
  modalDialog.value?.hideModal()
}
const open = (_record: Record<recordKey, string>) => {
  value.value = ''
  record.value = _record
  modalDialog.value?.show()
}
defineExpose({ open })
</script>
<template>
  <n-modal-dialog ref="modalDialog" :title="title" dialog-width="500px" @confirm="confirm">
    <template #content>
      <n-space vertical>
        <div>
          <span>{{ $t('commons.delete_confirm') }}</span>
          <span class="delete-tip"> DELETE-{{ record.name }}</span>
          <br />
        </div>
        <div v-if="withTip" class="tip">
          <span>
            <slot class="tip"></slot>
          </span>
        </div>
        <div>
          <n-input v-model:value="value" type="text" :placeholder="$t('commons.input_content')" />
        </div>
      </n-space>
    </template>
  </n-modal-dialog>
</template>

<style scoped>
.tip {
  margin-bottom: 20px;
  color: red;
}
.delete-tip {
  font-style: italic;
  font-weight: bold;
  white-space: pre-wrap;
}
</style>
