<script setup lang="ts">
import { NMenu, NSplit } from 'naive-ui'
import { h, ref, watch } from 'vue'
import { RouterLink, useRoute } from 'vue-router'
import ProjectSelect from './components/ProjectSelect.vue'
import { i18n } from '/@/i18n'

const activeKey = ref('')
const menuOptions = [
  {
    label: () =>
      h(
        RouterLink,
        {
          to: {
            path: '/project/home',
          },
        },
        { default: () => i18n.t('project.info') },
      ),
    key: '/project/home',
  },
  {
    label: () =>
      h(
        RouterLink,
        {
          to: {
            path: '/project/member',
          },
        },
        { default: () => i18n.t('project.member') },
      ),
    key: '/project/member',
  },
  {
    label: () =>
      h(
        RouterLink,
        {
          to: {
            path: '/project/usergroup',
          },
        },
        { default: () => i18n.t('project.group_permission') },
      ),
    key: '/project/usergroup',
  },
  {
    label: () =>
      h(
        RouterLink,
        {
          to: {
            path: '/project/template',
          },
        },
        { default: () => i18n.t('workspace.template_manage') },
      ),
    key: '/project/template',
  },
]
const currentProject = ref(sessionStorage.getItem('project_name'))
const route = useRoute()
watch(
  () => route.path,
  (newPath) => {
    activeKey.value = newPath
  },
  { immediate: true },
)
</script>
<template>
  <div>
    <n-split direction="horizontal" :default-size="0.1" :max="0.75" :min="0.25">
      <template #1>
        <project-select :project-name="currentProject || ''" />
      </template>
      <template #2>
        <n-menu v-model:value="activeKey" mode="horizontal" :options="menuOptions" responsive />
      </template>
    </n-split>
  </div>
</template>

<style scoped></style>
