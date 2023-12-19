<script setup lang="ts">
interface IProp {
  content: string
  disabled?: boolean
  type?: 'default' | 'tertiary' | 'primary' | 'success' | 'info' | 'warning' | 'error'
  size?: 'tiny' | 'small' | 'medium' | 'large'
  icon?: string
}
withDefaults(defineProps<IProp>(), {
  disabled: false,
  type: 'default',
  size: 'tiny',
  icon: 'i-mdi:help-circle',
})
const emits = defineEmits(['click'])
</script>
<template>
  <n-button secondary :disabled="disabled" :type="type" :size="size" @click="emits('click')">
    <template #icon>
      <n-icon><span :class="icon" /></n-icon>
    </template>
    {{ content }}
  </n-button>
</template>

<style scoped>
.n-button {
  padding: 8px 10px;
}
</style>
