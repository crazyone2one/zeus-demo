<script setup lang="ts">
import { ref } from 'vue'
import NTipButton from './NTipButton.vue'
interface IProp {
  tip: string
  icon?: string
  type?: 'default' | 'tertiary' | 'primary' | 'success' | 'info' | 'warning' | 'error'
  size?: 'tiny' | 'small' | 'medium' | 'large'
  disabled?: boolean
  isDivButton?: boolean
  isTextButton?: boolean
  isMoreOperate?: boolean
}
withDefaults(defineProps<IProp>(), {
  icon: 'i-mdi:help-circle',
  type: 'primary',
  size: 'tiny',
  disabled: false,
  isDivButton: false,
  isTextButton: false,
  isMoreOperate: false,
})
const isReadOnly = ref(false)
const emit = defineEmits(['exec'])
const exec = () => emit('exec')
</script>
<template>
  <div>
    <n-tooltip v-if="isDivButton" trigger="hover" placement="bottom">
      <template #trigger>
        <n-button circle type="primary" size="tiny" :disabled="isReadOnly" @click="exec">
          <div style="transform: scale(0.8)">
            <span style="margin-left: -4px; line-height: 27px">{{ tip }}</span>
          </div>
        </n-button>
      </template>
      {{ tip }}
    </n-tooltip>
    <n-button v-else-if="isTextButton" text type="primary" size="tiny" :disabled="isReadOnly" @click="exec">
      {{ tip }}
    </n-button>
    <n-tip-button
      v-else
      :type="type"
      :tip="tip"
      :icon="icon"
      size="tiny"
      :disabled="disabled || isReadOnly"
      @click="exec"
    />
  </div>
</template>

<style scoped></style>
