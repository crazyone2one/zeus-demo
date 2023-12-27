<script setup lang="ts">
import { MenuOption, NIcon, NLayoutSider, NMenu } from 'naive-ui'
import { h, ref, watch } from 'vue'
import { RouterLink, useRoute } from 'vue-router'
import { i18n } from '/@/i18n'
const collapsed = ref(false)
const activeKey = ref<string | null>(null)
// 菜单数据
const menuOptions: MenuOption[] = [
  {
    label: i18n.t('commons.my_workstation'),
    key: 'workstation',
    icon: () =>
      h(NIcon, null, {
        default: () => h('span', { class: 'i-my-icons-workspace' }),
      }),
  },
  {
    label: i18n.t('test_track.test_track'),
    key: 'test_track',
    disabled: true,
    icon: () =>
      h(NIcon, null, {
        default: () => h('span', { class: 'i-my-icons-track' }),
      }),
  },
  {
    label: i18n.t('commons.api'),
    key: 'api',
    disabled: true,
    icon: () =>
      h(NIcon, null, {
        default: () => h('span', { class: 'i-my-icons-api' }),
      }),
  },
  {
    label: i18n.t('commons.performance'),
    key: 'performance',
    disabled: true,
    icon: () =>
      h(NIcon, null, {
        default: () => h('span', { class: 'i-my-icons-performance' }),
      }),
  },
  {
    label: i18n.t('commons.report_statistics.title'),
    key: 'report-statistics',
    disabled: true,
    icon: () =>
      h(NIcon, null, {
        default: () => h('span', { class: 'i-my-icons-report' }),
      }),
  },
  {
    // label: i18n.t("commons.project_setting"),
    label: () => h(RouterLink, { to: { path: '/project' } }, { default: () => i18n.t('commons.project_setting') }),
    key: '/project/home',
    icon: () =>
      h(NIcon, null, {
        default: () => h('span', { class: 'i-mdi:folder-cog-outline' }),
      }),
  },
  {
    label: i18n.t('commons.system_setting'),
    key: 'pinball-1973',
    icon: () =>
      h(NIcon, null, {
        default: () => h('span', { class: 'i-my-icons-settings' }),
      }),
    children: [
      {
        type: 'group',
        label: i18n.t('commons.system'),
        key: 'people',
        children: [
          {
            label: () => h(RouterLink, { to: { name: 'user' } }, { default: () => i18n.t('commons.user') }),
            key: '/setting/user',
          },
          {
            label: () => h(RouterLink, { to: { name: 'workspace' } }, { default: () => i18n.t('commons.workspace') }),
            key: '/setting/workspace',
          },
          {
            label: () =>
              h(RouterLink, { to: { name: 'usergroup' } }, { default: () => i18n.t('group.group_permission') }),
            key: '/setting/usergroup',
          },
        ],
      },
      {
        type: 'group',
        label: i18n.t('commons.workspace'),
        key: 'people',
        children: [
          {
            label: () =>
              h(
                RouterLink,
                {
                  to: {
                    name: 'project',
                    params: { type: 'all' },
                  },
                },
                { default: () => i18n.t('project.manager') },
              ),
            key: '/setting/project/all',
          },
        ],
      },
    ],
  },
  {
    label: 'demo',
    key: 'demo',
    disabled: true,
    children: [
      {
        label: () => h(RouterLink, { to: { name: 'upload' } }, { default: () => 'upload' }),
        key: 'demo1',
      },
    ],
  },
]
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
  <n-layout-sider
    bordered
    collapse-mode="width"
    :collapsed-width="64"
    :width="240"
    :collapsed="collapsed"
    show-trigger
    :native-scrollbar="false"
    @collapse="collapsed = true"
    @expand="collapsed = false"
  >
    <n-menu
      v-model:value="activeKey"
      :collapsed="collapsed"
      :collapsed-width="64"
      :collapsed-icon-size="22"
      :options="menuOptions"
    />
  </n-layout-sider>
</template>

<style scoped></style>
