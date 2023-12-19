<script setup lang="ts">
import { computed } from 'vue'
import { IQueryParam } from '../api/interface'
import { i18n } from '../i18n'
import NTableSearchBar from './NTableSearchBar.vue'
interface Props {
  condition?: IQueryParam
  showBaseSearch?: boolean
  baseSearchTip?: string
}
const prop = withDefaults(defineProps<Props>(), {
  showBaseSearch: true,
  condition: () => {
    return {
      pageNumber: 1,
      pageSize: 10,
      name: '',
    }
  },
  baseSearchTip: () => i18n.t('commons.search_by_name_or_id'),
})
const emits = defineEmits(['update:condition', 'search'])
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
const handleSearch = () => emits('search')
</script>
<template>
  <div>
    <n-table-search-bar v-if="showBaseSearch" :condition="_condition" :tip="baseSearchTip" @change="handleSearch" />
  </div>
</template>

<style scoped></style>
