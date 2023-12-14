import { RouteRecordRaw, createRouter, createWebHistory } from 'vue-router'

// Define some routes
const routes: RouteRecordRaw[] = [{ path: '/', component: () => import(`/@/layout/index.vue`) }]
// Create the router instance and pass the `routes` option
const router = createRouter({
  // 4. Provide the history implementation to use. We are using the hash history for simplicity here.
  history: createWebHistory(),
  routes, // short for `routes: routes`
  scrollBehavior() {
    // 始终滚动到顶部
    return { top: 0 }
  },
})

export default router
