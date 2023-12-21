export interface IUserGroup {
  id: string
  userId: string
  group_code: string
  sourceId: string
  userGroupPermissions?: Array<IUserGroupPermission>
  group?: IGroup
}
export interface IGroup {
  id: string
  name: string
  description: string
  syste: string
  type: string
  groupCode: string
}
export interface IGroupResource {
  id: string
  name: string
}
export interface IGroupPermission {
  id: string
  name: string
  resourceId: string
}
export interface IUserGroupPermission {
  id: string
  groupCode: string
  permissionId: string
  moduleId: string
}
export interface IGroupResourceDto {
  group: IGroup
  userGroupPermissions: Array<IUserGroupPermission>
  type: string
  permissions: Array<IGroupPermission>
  resource: IGroupResource
}
