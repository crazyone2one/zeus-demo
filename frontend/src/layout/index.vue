<script setup lang="ts">
import { NLayout, NLayoutHeader, NH2, NLayoutFooter } from 'naive-ui'
import { useClientHeight } from '/@/composables/client-height'
import UserMenu from './components/header/UserMenu.vue'
import sidebar from './components/sidebar/index.vue'
const { height } = useClientHeight()
</script>
<template>
  <n-layout :style="{ height: height + 'px' }">
    <n-layout-header class="header" bordered>
      <div class="header-user">
        <user-menu />
      </div>
    </n-layout-header>
    <n-layout position="absolute" style="top: 50px; bottom: 50px" has-sider>
      <sidebar />
      <n-layout content-style="padding: 2px;" :native-scrollbar="false">
        <n-h2>平山道</n-h2>
        <n-h2>平山道</n-h2>
        <n-h2>平山道</n-h2>
        <n-h2>平山道</n-h2>
        <n-h2>平山道</n-h2>
        <n-h2>平山道</n-h2>
        <n-h2>平山道</n-h2>
        <n-h2>平山道</n-h2>
        <n-h2>平山道</n-h2>
        <n-h2>平山道</n-h2>
        <n-h2>平山道</n-h2>
        <n-h2>平山道</n-h2>
        <n-h2>平山道</n-h2>
        <n-h2>平山道</n-h2>
        <n-h2>平山道</n-h2>
        <n-h2>平山道</n-h2>
      </n-layout>
    </n-layout>
    <n-layout-footer position="absolute" style="height: 50px; padding: 10px" bordered>
      zeus / the2ndindec@gmail.com
    </n-layout-footer>
  </n-layout>
</template>

<style scoped>
.header {
  display: flex;
  height: 50px;
  padding: 12px;
  align-items: center;
  .header-user {
    flex: 1;
    display: flex;
    justify-content: flex-end;
    padding-right: 10px;
  }
}
</style>
