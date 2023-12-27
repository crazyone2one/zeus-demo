import { RouteRecordRaw } from 'vue-router'
const Project: Array<RouteRecordRaw> = [
  {
    path: '/project',
    name: 'project-manage',
    redirect: '/project/home',
    component: () => import(`/@/views/project-manage/ProjectSetting.vue`),
    children: [
      { path: '/project/home', component: () => import('/@/views/project-manage/home/ProjectHome.vue') },
      { path: '/project/member', component: () => import('/@/views/project-manage/components/member/index.vue') },
      {
        path: '/project/usergroup',
        component: () => import('/@/views/project-manage/components/user-group/index.vue'),
      },
      { path: '/project/template', component: () => import('/@/views/project-manage/components/template/index.vue') },
    ],
  },
]
export default Project
