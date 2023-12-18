<script setup lang="ts">
import { NLayout, NLayoutHeader, NLayoutFooter } from 'naive-ui'
import { useClientHeight } from '/@/composables/client-height'
import UserMenu from './components/header/UserMenu.vue'
import sidebar from './components/sidebar/index.vue'
import Logo from '/@/layout/components/logo/index.vue'
const { height } = useClientHeight()
</script>
<template>
  <n-layout :style="{ height: height + 'px' }">
    <n-layout-header
      class="nav"
      style="--side-padding: 32px; grid-template-columns: calc(272px - var(--side-padding)) 1fr auto"
      bordered
    >
      <logo />
      <div style="display: flex; align-items: center"></div>
      <div class="nav-end">
        <user-menu />
      </div>
    </n-layout-header>
    <n-layout position="absolute" style="top: 64px; bottom: 50px" has-sider>
      <sidebar />
      <n-layout content-style="padding: 2px;" :native-scrollbar="false">
        <router-view />
      </n-layout>
    </n-layout>
    <n-layout-footer position="absolute" style="height: 50px; padding: 10px" bordered>
      zeus / the2ndindec@gmail.com
    </n-layout-footer>
  </n-layout>
</template>

<style scoped>
.nav {
  display: grid;
  grid-template-rows: calc(var(--header-height) - 1px);
  padding: 0 var(--side-padding);
}
.nav .ui-logo {
  align-items: center;
}

.nav-end {
  align-items: center;
  display: flex;
}
</style>
