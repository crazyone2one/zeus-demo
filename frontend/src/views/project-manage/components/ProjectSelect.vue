<script setup lang="ts">
import { useRequest } from 'alova'
import { SelectOption } from 'naive-ui'
import { onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { IProjectItem, getUserProjectList, switchProject } from '/@/api/modules/project'
import { i18n } from '/@/i18n'
import { getCurrentProjectId, getCurrentUserId, getCurrentWorkspaceId } from '/@/utils/token'
interface IProp {
  projectName: string
}
const route = useRoute()
const router = useRouter()
const props = defineProps<IProp>()
const currentProject = ref<string | undefined>(props.projectName)
const currentProjectId = ref<string | undefined>('')
const searchString = ref<string | undefined>('')
const searchArray = ref<IProjectItem[]>([])
const options = ref<Array<SelectOption>>([])
const userId = ref(getCurrentUserId())
const { send } = useRequest((val) => getUserProjectList(val), { immediate: false })
const { send: swp } = useRequest((val) => switchProject(val), { immediate: false })
const init = () => {
  let data = {
    userId: userId.value,
    workspaceId: route.params.workspaceId || getCurrentWorkspaceId(),
  }
  send(data).then((res) => {
    const tmp = res
    searchArray.value = tmp
    options.value = tmp.map((item: IProjectItem) => {
      return {
        label: item.name,
        value: item.id,
      }
    })
    let projectId = getCurrentProjectId()
    if (projectId) {
      if (searchArray.value.length > 0 && searchArray.value.map((p) => p.id).indexOf(projectId) === -1) {
        change(options.value[0].value as string)
      }
    } else {
      if (options.value.length > 0) {
        change(options.value[0].value as string)
      }
    }
    changeProjectName(projectId)
  })
}
const change = (_projectId: string) => {
  let currentProjectId = getCurrentProjectId()
  if (_projectId === currentProjectId) {
    return
  }
  swp({ id: userId.value, lastProjectId: _projectId }).then((res) => {
    sessionStorage.setItem('project_id', res.lastProjectId)
    changeProjectName(_projectId)
  })
}
const changeProjectName = (projectId: string) => {
  if (projectId) {
    const project = searchArray.value.find((item) => item.id === projectId)
    if (project) {
      currentProject.value = project.name
      currentProjectId.value = project.id
      sessionStorage.setItem('project_name', currentProject.value)
    }
  } else {
    currentProject.value = i18n.t('project.select')
  }
}
watch(
  () => searchString.value,
  (val) => {
    console.log(`output->val`, val)
  },
)
const handleAddOrList = (val: boolean) => {
  val ? router.push('/project/create') : router.push('/project/all')
}
onMounted(() => {
  init()
})
</script>
<template>
  <n-popover trigger="hover">
    <template #trigger>
      <n-popselect v-model:value="currentProjectId" :options="options" trigger="click" scrollable size="small">
        <span class="project-name ml-3 m-2.5"> {{ $t('commons.project') }}:{{ currentProject || '弹出选择' }}</span>
        <template #header>
          <n-input v-model:value="searchString" round :placeholder="$t('project.search_by_name')" size="tiny">
            <template #prefix>
              <n-icon>
                <span class="i-ion:ios-search" />
              </n-icon>
            </template>
          </n-input>
        </template>
        <template #empty>
          <span style="font-size: 15px; color: #8a8b8d">
            {{ $t('project.no_data') }}
          </span>
        </template>
        <template #action>
          <div v-permission="['WORKSPACE_PROJECT_MANAGER:READ']">
            <n-space vertical>
              <n-button
                v-permission="['WORKSPACE_PROJECT_MANAGER:READ+CREATE']"
                secondary
                size="tiny"
                @click="handleAddOrList(true)"
              >
                <template #icon>
                  <n-icon>
                    <span class="i-mdi:plus" />
                  </n-icon>
                </template>
                {{ $t('project.create') }}
              </n-button>
              <n-button
                v-permission="['WORKSPACE_PROJECT_MANAGER:READ']"
                secondary
                size="tiny"
                @click="handleAddOrList(false)"
              >
                <template #icon>
                  <n-icon>
                    <span class="i-mdi:format-list-numbered-rtl" />
                  </n-icon>
                </template>
                {{ $t('commons.show_all') }}
              </n-button>
            </n-space>
          </div>
        </template>
      </n-popselect>
    </template>
    <span>{{ currentProject }}</span>
  </n-popover>
</template>

<style scoped>
.project-name {
  display: inline-block;
  width: 160px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
