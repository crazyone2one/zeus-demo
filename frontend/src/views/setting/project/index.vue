<script setup lang="ts">
import { usePagination } from '@alova/scene-vue'
import { DataTableColumns, DataTableRowKey } from 'naive-ui'
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import EditProject from './components/EditProject.vue'
import { IQueryParam } from '/@/api/interface'
import { IProjectItem, queryProjectPage } from '/@/api/modules/project'
import NPagination from '/@/components/NPagination.vue'
import NTableHeader from '/@/components/NTableHeader.vue'
import { i18n } from '/@/i18n'
import { getCurrentWorkspaceId } from '/@/utils/token'

const condition = reactive<IQueryParam>({
  name: '',
  pageNumber: 1,
  pageSize: 5,
})
const router = useRouter()
const editProject = ref<InstanceType<typeof EditProject> | null>(null)
const columns: DataTableColumns<IProjectItem> = [
  {
    type: 'selection',
    // disabled (row: RowData) {
    //   return row.name === 'Edward King 3'
    // }
  },
  {
    title: i18n.t('commons.name'),
    key: 'name',
  },
  {
    title: i18n.t('commons.description'),
    key: 'description',
  },
  {
    title: i18n.t('commons.member'),
    key: 'memberSize',
  },
  {
    title: i18n.t('commons.operating'),
    key: 'operating',
  },
]

const rowKey = (row: IProjectItem) => row.id
const checkedRowKeysRef = ref<DataTableRowKey[]>([])
const handleCheck = (rowKeys: DataTableRowKey[]) => (checkedRowKeysRef.value = rowKeys)
const handlePrevPage = (val: number) => {
  pageSize.value = val
}

const {
  // 加载状态
  loading,
  data,
  page,
  pageSize,
  total,
  send: loadTableData,
} = usePagination(
  // Method实例获取函数，它将接收page和pageSize，并返回一个Method实例
  (page, pageSize) => queryProjectPage(page, pageSize, condition),
  {
    // 请求前的初始数据（接口返回的数据格式）
    initialData: {
      total: 0,
      data: [],
    },
    total: (response) => response.totalRow,
    data: (response) => response.records,
    initialPage: 1, // 初始页码，默认为1
    initialPageSize: 10, // 初始每页数据条数，默认为10
    // watchingStates: [condition],
    debounce: 300, // 防抖参数，单位为毫秒数，也可以设置为数组对watchingStates单独设置防抖时间
    immediate: false,
  },
)

const handleList = () => {
  loadTableData()
}
const handleCreate = () => {
  editProject.value?.open()
}
const route = useRoute()
onMounted(() => {
  condition.workspaceId = getCurrentWorkspaceId()
  loadTableData()
  const tmpPath = route.path.split('/')[2]
  if (tmpPath === 'create') {
    editProject.value?.open()
    router.push('/project/all')
  } else {
    router.push('/project/all')
  }
})
</script>

<template>
  <n-spin :show="loading">
    <n-card>
      <template #header>
        <n-table-header
          :condition="condition"
          :create-tip="$t('project.create')"
          :create-permission="['WORKSPACE_PROJECT_MANAGER:READ+CREATE']"
          @search="handleList"
          @create="handleCreate"
        />
      </template>
      <n-data-table :columns="columns" :data="data" :row-key="rowKey" @update:checked-row-keys="handleCheck" />
      <n-pagination :total="total" :page-size="pageSize" :page="page" @update:page-size="handlePrevPage" />
    </n-card>
  </n-spin>
  <edit-project ref="editProject" @refresh="loadTableData" />
</template>

<style scoped></style>
