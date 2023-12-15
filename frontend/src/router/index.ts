import { RouteRecordRaw, createRouter, createWebHashHistory } from 'vue-router'

// Define some routes
const routes: RouteRecordRaw[] = [
  { path: '/', component: () => import(`/@/layout/index.vue`) },
  { path: '/login', name: 'login', component: () => import('/@/views/login/index.vue') },
  // 将匹配所有内容并将其放在 `$route.params.pathMatch` 下
  { path: '/:pathMatch(.*)*', name: 'NotFound', component: () => import(`/@/views/error/NotFound.vue`) },
]
// Create the router instance and pass the `routes` option
const router = createRouter({
  // Provide the history implementation to use. We are using the hash history for simplicity here.
  history: createWebHashHistory(),
  routes, // short for `routes: routes`
  scrollBehavior() {
    // 始终滚动到顶部
    return { top: 0 }
  },
})

export default router
