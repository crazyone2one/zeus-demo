<script setup lang="ts">
import { usePagination } from '@alova/scene-vue'
import { useRequest } from 'alova'
import { DataTableColumns, DataTableRowKey } from 'naive-ui'
import { h, onMounted, reactive, ref, withDirectives } from 'vue'
import EditPermission from './components/EditPermission.vue'
import EditUserGroup from './components/EditUserGroup.vue'
import { IQueryParam } from '/@/api/interface'
import { IGroup, delUserGroupById, queryGroupPage } from '/@/api/modules/group'
import NDeleteConfirm from '/@/components/NDeleteConfirm.vue'
import NPagination from '/@/components/NPagination.vue'
import NTableHeader from '/@/components/NTableHeader.vue'
import NTableOperator from '/@/components/NTableOperator.vue'
import NTableOperatorButton from '/@/components/NTableOperatorButton.vue'
import permission from '/@/directive/permission'
import { i18n } from '/@/i18n'
import { getCurrentWorkspaceId } from '/@/utils/token'

const condition = reactive<IQueryParam>({
  name: '',
  pageNumber: 1,
  pageSize: 5,
})
const editUserGroup = ref<InstanceType<typeof EditUserGroup> | null>(null)
const editPermission = ref<InstanceType<typeof EditPermission> | null>(null)
const deleteConfirm = ref<InstanceType<typeof NDeleteConfirm> | null>(null)
const columns: DataTableColumns<IGroup> = [
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
    title: i18n.t('group.type'),
    key: 'type',
  },
  {
    title: i18n.t('commons.member'),
    key: 'memberSize',
  },
  {
    title: i18n.t('group.scope'),
    key: 'memberSize',
  },
  {
    title: i18n.t('commons.description'),
    key: 'description',
  },
  {
    title: i18n.t('commons.operating'),
    key: 'operating',
    width: 200,
    fixed: 'right',
    render(row) {
      return h(
        NTableOperator,
        {
          editPermission: ['SYSTEM_GROUP:READ+EDIT'],
          deletePermission: ['SYSTEM_GROUP:READ+DELETE'],
          onEditClick: () => handleEdit(row),
          onDeleteClick: () => handleDelete(row),
        },
        {
          middle: () => {
            return withDirectives(
              h(
                NTableOperatorButton,
                { tip: i18n.t('group.set_permission'), icon: 'i-mdi:tools', onExec: () => setPermission(row) },
                {},
              ),
              [[permission, ['SYSTEM_GROUP:READ+SETTING_PERMISSION']]],
            )
          },
        },
      )
    },
  },
]

const rowKey = (row: IGroup) => row.id
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
  (page, pageSize) => queryGroupPage(page, pageSize, condition),
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
  editUserGroup.value?.open('create', i18n.t('group.create'))
}
const handleEdit = (row: IGroup) => {
  if (row.groupCode === 'admin') {
    window.$message.warning(i18n.t('group.admin_not_allow_edit'))
    return
  }
  editUserGroup.value?.open('edit', i18n.t('group.edit'), row)
}
const handleDelete = (row: IGroup) => {
  deleteConfirm.value?.open(row)
}
const { send: deleteUg } = useRequest((id) => delUserGroupById(id), { immediate: false })
const _handleDel = (row: IGroup) => {
  deleteUg(row.id).then(() => {
    window.$message.success(i18n.t('commons.delete_success'))
    loadTableData()
  })
}
const setPermission = (row: IGroup) => {
  editPermission.value?.open(row)
}
onMounted(() => {
  condition.workspaceId = getCurrentWorkspaceId()
  loadTableData()
})
</script>

<template>
  <n-spin :show="loading">
    <n-card>
      <template #header>
        <n-table-header
          :condition="condition"
          :create-tip="$t('group.create')"
          :create-permission="['SYSTEM_GROUP:READ+CREATE']"
          @search="handleList"
          @create="handleCreate"
        />
      </template>
      <n-data-table :columns="columns" :data="data" :row-key="rowKey" @update:checked-row-keys="handleCheck" />
      <n-pagination :total="total" :page-size="pageSize" :page="page" @update:page-size="handlePrevPage" />
    </n-card>
  </n-spin>
  <edit-user-group ref="editUserGroup" @refresh="loadTableData" />
  <edit-permission ref="editPermission" />
  <n-delete-confirm ref="deleteConfirm" :title="$t('group.delete')" @delete="_handleDel" />
</template>

<style scoped></style>
