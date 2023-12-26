<script setup lang="ts">
interface IProp {
  tip: string
  icon?: string
  type?: 'default' | 'tertiary' | 'primary' | 'success' | 'info' | 'warning' | 'error'
  size?: 'tiny' | 'small' | 'medium' | 'large'
  disabled?: boolean
}
withDefaults(defineProps<IProp>(), {
  icon: 'i-mdi:help-circle',
  type: 'default',
  size: 'tiny',
  disabled: false,
})
const emit = defineEmits(['click'])
</script>
<template>
  <n-tooltip trigger="hover" placement="bottom">
    <template #trigger>
      <n-button circle :type="type" :size="size" :disabled="disabled" @click="emit('click')">
        <template #icon>
          <n-icon><span :class="icon" /></n-icon>
        </template>
      </n-button>
    </template>
    {{ tip }}
  </n-tooltip>
</template>

<style scoped></style>
