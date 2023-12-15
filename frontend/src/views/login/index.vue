<script setup lang="ts">
import { ref, computed } from 'vue'
import { ILoginParams, loginAPI } from '/@/api/modules/auth'
import { NH1 } from 'naive-ui'
import { useAuthStore } from '/@/store/modules/auth-store'
import { useRequest } from 'alova'

const model = ref<ILoginParams>({
  username: '',
  password: '',
})
const store = useAuthStore()
const disabled = computed<boolean>(() => model.value.username === '' || model.value.password === '')
const { loading, send } = useRequest(loginAPI(model.value), {
  immediate: false,
})
const handleLogin = () => {
  send()
    .then((res) => {
      const _res = res
      const { accessToken, refreshToken, userId } = _res
      store.$patch((state) => {
        state.accessToken = accessToken
        state.refreshToken = refreshToken
        state.userId = userId
      })
    })
    .catch((err) => window.$message.error(err.message))
}
</script>
<template>
  <n-h1 style="--font-size: 60px; --font-weight: 100">Sign in to your account </n-h1>
  <n-card size="large" style="--padding-bottom: 30px" class="shadow-lg shadow-rose-500/50">
    <!-- <n-h2 style="--font-weight: 400">Sign-in</n-h2> -->
    <n-form size="large" :model="model">
      <n-form-item-row path="username">
        <n-input v-model:value="model.username"> </n-input>
      </n-form-item-row>
      <n-form-item-row path="password">
        <n-input v-model:value="model.password" type="password" show-password-on="mousedown" @keyup.enter="handleLogin">
        </n-input>
      </n-form-item-row>
    </n-form>
    <n-button type="primary" block :loading="loading" :disabled="disabled" @click="handleLogin"> login </n-button>
  </n-card>
</template>

<style scoped>
.n-h1 {
  margin: 20vh auto 20px;
  text-align: center;
  letter-spacing: 5px;
  opacity: 0.8;
}

.n-card {
  margin: 0 auto;
  max-width: 380px;
  /* box-shadow: var(--box-shadow); */
}
</style>
