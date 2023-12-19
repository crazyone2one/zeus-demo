<script setup lang="ts">
import { computed } from 'vue'
import { i18n } from '../i18n'
import { IQueryParam } from '../api/interface'

interface IProp {
  condition: IQueryParam
  tip?: string
}
const prop = withDefaults(defineProps<IProp>(), {
  tip: () => i18n.t('commons.search_by_name'),
})
const emits = defineEmits(['update:condition', 'change'])
const _condition = computed({
  // getter
  get() {
    return prop.condition
  },
  // setter
  set(newValue) {
    emits('update:condition', newValue)
  },
})
const handleSearch = () => emits('change')
</script>
<template>
  <n-input v-model:value="_condition.name" type="text" size="tiny" :placeholder="tip" @change="handleSearch">
    <template #prefix>
      <n-icon><span class="i-mdi:magnify" /></n-icon>
    </template>
  </n-input>
</template>

<style scoped></style>
