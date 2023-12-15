<script setup lang="ts">
import { useRequest } from 'alova'
import { NAvatar, NText } from 'naive-ui'
import { h } from 'vue'
import { logoutAPI } from '/@/api/modules/auth'
import { useAuthStore } from '/@/store/modules/auth-store'
import router from '/@/router'

const store = useAuthStore()
const renderCustomHeader = () => {
  return h(
    'div',
    {
      style: 'display: flex; align-items: center; padding: 8px 12px;',
    },
    [
      h(NAvatar, {
        round: true,
        style: 'margin-right: 12px;',
        src: 'https://07akioni.oss-cn-beijing.aliyuncs.com/demo1.JPG',
      }),
      h('div', null, [
        h('div', null, [h(NText, { depth: 2 }, { default: () => '打工仔' })]),
        h('div', { style: 'font-size: 12px;' }, [
          h(NText, { depth: 3 }, { default: () => '毫无疑问，你是办公室里最亮的星' }),
        ]),
      ]),
    ],
  )
}
const userOptions = [
  { key: 'header', type: 'render', render: renderCustomHeader },
  { key: 'header-divider', type: 'divider' },
  { label: '处理群消息 342 条', key: 'stmt1' },
  { label: '加入群 17 个', key: 'stmt3' },
  { label: '退出登录', key: 'stmt4' },
]
const { send, loading } = useRequest(logoutAPI(), { immediate: false })
const handleSelect = (key: string) => {
  // logout
  if (key === 'stmt4') {
    window.$dialog.warning({
      title: '',
      maskClosable: false,
      content: () => '是否退出系统',
      positiveText: '确定',
      negativeText: '不确定',
      loading: loading.value,
      onPositiveClick() {
        send().then(() => {
          store.$reset()
          const route = router.currentRoute
          router.push(
            `/login?redirect=${route.value.path}&params=${JSON.stringify(
              route.value.query ? route.value.query : route.value.params,
            )}`,
          )
        })
      },
    })
  }
}
</script>
<template>
  <n-dropdown trigger="click" :options="userOptions" @select="handleSelect">
    <n-text class="nav-picker padded"> zeus </n-text>
  </n-dropdown>
</template>

<style scoped>
.nav-picker {
  margin-right: 4px;
}
.nav-picker.padded {
  padding: 0 10px;
}

.nav-picker:last-child {
  margin-right: 0;
}
.user-menu {
  display: flex;
  height: 50px;
  flex-shrink: 0;
  margin-left: 50px;
  .set-item {
    display: flex;
    align-items: center;
    justify-content: center;
    padding-left: 12px;
    padding-right: 12px;
    cursor: pointer;
    flex-shrink: 0;
  }
}
</style>
