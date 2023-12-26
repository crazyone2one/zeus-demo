<script setup lang="ts">
import { useRequest } from 'alova'
import { NPopselect, SelectOption } from 'naive-ui'
import { computed, onMounted, ref, watch } from 'vue'
import { getUserWorkspaceList, switchWorkspace } from '/@/api/modules/workspace'
import { getCurrentWorkspaceId } from '/@/utils/token'

const _workspaceId = computed(() => {
  return getCurrentWorkspaceId()
})
const workspaceList = ref<SelectOption[]>([])
const wsListCopy = ref<SelectOption[]>([])
const workspaceId = ref(_workspaceId.value)
const currentWorkspaceName = ref<string>(sessionStorage.getItem('workspace_name') || '')
const { send, onSuccess } = useRequest(getUserWorkspaceList(), { immediate: false })
const initMenuData = () => {
  send()
}
onSuccess((response) => {
  if (response.data) {
    response.data.forEach((item) => {
      const option: SelectOption = {}
      option.label = item.name
      option.value = item.id
      workspaceList.value.push(option)
      wsListCopy.value.push(option)
    })
    let workspace = response.data.filter((r) => r.id === workspaceId.value)
    if (workspace.length > 0) {
      currentWorkspaceName.value = workspace[0].name
      workspaceList.value = workspaceList.value.filter((r) => r.value !== workspaceId.value)
      workspaceList.value.unshift({ label: currentWorkspaceName.value, value: workspaceId.value })
    } else {
      currentWorkspaceName.value = response.data[0].name
      _changeWs(response.data[0].id)
    }
  }
})
const { send: changeWs } = useRequest((value) => switchWorkspace(value), { immediate: false })
const _changeWs = (wsId: string) => {
  if (wsId) {
    changeWs(wsId).then((res) => {
      sessionStorage.setItem('workspace_id', res.lastWorkspaceId)
      sessionStorage.setItem('project_id', res.lastProjectId)
      window.location.reload()
    })
  }
}
const handleChange = (value: string) => {
  _changeWs(value)
}
onMounted(() => {
  initMenuData()
})

watch(
  () => currentWorkspaceName.value,
  (newValue) => {
    sessionStorage.setItem('workspace_name', newValue)
  },
)
// workspaceList.value = data.value
</script>
<template>
  <n-popselect
    v-model:value="workspaceId"
    :options="workspaceList"
    trigger="click"
    scrollable
    @update-value="handleChange"
  >
    <n-button>{{ currentWorkspaceName || '未选择workspace' }}</n-button>
    <template #header> 不知道放些什么 </template>
    <template #empty> 没啥看的，这里是空的 </template>
    <template #action> 如果你点开了这个例子，你可能需要它 </template>
  </n-popselect>
</template>

<style scoped></style>
