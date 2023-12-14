<script setup lang="ts">
import { NLayout, NLayoutSider, NLayoutHeader, NH2, NLayoutFooter } from 'naive-ui'
import { useClientHeight } from '/@/composables/client-height'
const { height } = useClientHeight()
</script>
<template>
  <n-layout :style="{ height: height + 'px' }">
    <n-layout-header style="height: 64px; padding: 24px" bordered> 颐和园路 </n-layout-header>
    <n-layout position="absolute" style="top: 64px; bottom: 64px" has-sider>
      <n-layout-sider
        :native-scrollbar="false"
        bordered
        collapse-mode="width"
        :collapsed-width="64"
        :width="240"
        show-trigger="arrow-circle"
        content-style="padding: 24px;"
      >
        <n-h2>海淀桥</n-h2>
        <n-h2>海淀桥</n-h2>
        <n-h2>海淀桥</n-h2>
        <n-h2>海淀桥</n-h2>
        <n-h2>海淀桥</n-h2>
        <n-h2>海淀桥</n-h2>
        <n-h2>海淀桥</n-h2>
        <n-h2>海淀桥</n-h2>
        <n-h2>海淀桥</n-h2>
        <n-h2>海淀桥</n-h2>
        <n-h2>海淀桥</n-h2>
        <n-h2>海淀桥</n-h2>
      </n-layout-sider>
      <n-layout content-style="padding: 24px;" :native-scrollbar="false">
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
    <n-layout-footer position="absolute" style="height: 30px; padding: 3px" bordered>
      zeus / the2ndindec@gmail.com
    </n-layout-footer>
  </n-layout>
</template>

<style scoped></style>
