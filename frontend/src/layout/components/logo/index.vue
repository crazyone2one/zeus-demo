<script setup lang="ts">
import { NText } from 'naive-ui'
import logo from '../../../assets/387941.svg'
</script>
<template>
  <n-text class="ui-logo">
    <img :src="logo" />
    <span>Zeus</span>
  </n-text>
</template>

<style scoped>
.ui-logo {
  cursor: pointer;
  display: flex;
  font-size: 18px;
}
.ui-logo > img {
  height: 32px;
  margin-right: 12px;
  width: 32px;
}
</style>
