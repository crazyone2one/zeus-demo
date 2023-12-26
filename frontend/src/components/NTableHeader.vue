<script setup lang="ts">
import { i18n } from '../i18n'
import NTableButton from './NTableButton.vue'
import NSearch from './NSearch.vue'
import { IQueryParam } from '../api/interface'
import { computed } from 'vue'
interface IProp {
  showCreate?: boolean
  showImport?: boolean
  showRun?: boolean
  createTip?: string
  importTip?: string
  runTip?: string
  createPermission?: Array<string>
  uploadPermission?: Array<string>
  tip?: string
  haveSearch?: boolean
  condition: IQueryParam
}
const prop = withDefaults(defineProps<IProp>(), {
  showCreate: true,
  showImport: false,
  showRun: false,
  createTip: () => i18n.t('commons.create'),
  importTip: () => i18n.t('commons.import'),
  runTip: '',
  createPermission: () => [],
  uploadPermission: () => [],
  haveSearch: true,
  tip: () => i18n.t('commons.search_by_name'),
})
const emits = defineEmits(['update:condition', 'search', 'create', 'import', 'runTest'])
const _condition = computed({
  // getter
  get() {
    return prop.condition
  },
  // setter
  set(newValue) {
    console.log(`output->`, newValue)
    emits('update:condition', newValue)
  },
})
const handleSearch = () => {
  emits('search')
}
</script>
<template>
  <div>
    <n-space justify="space-between">
      <span class="operate-button">
        <n-table-button
          v-if="showCreate"
          v-permission="createPermission"
          :content="createTip"
          icon="i-mdi:plus"
          @click="emits('create')"
        />
        <n-table-button
          v-if="showImport"
          :content="importTip"
          icon="i-mdi:cloud-upload-outline"
          @click="emits('import')"
        />
        <n-table-button
          v-if="showRun"
          type="primary"
          :content="runTip"
          icon="i-mdi:motion-play-outline"
          @click="emits('runTest')"
        />
        <slot name="button"></slot>
      </span>
      <span>
        <slot name="searchBarBefore"></slot>
        <n-search
          :show-base-search="haveSearch"
          :base-search-tip="tip"
          :condition="_condition"
          @search="handleSearch"
        />
      </span>
    </n-space>
  </div>
</template>

<style scoped></style>
