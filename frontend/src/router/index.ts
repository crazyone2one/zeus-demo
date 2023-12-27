import { RouteRecordRaw, createRouter, createWebHashHistory } from 'vue-router'
import { i18n } from '../i18n'
import Project from './modules/project-manage'
import Setting from './modules/setting'
import { useAuthStore } from '/@/store/modules/auth-store'

// Define some routes
const routes: RouteRecordRaw[] = [
  { path: '/', component: () => import(`/@/layout/index.vue`), children: [...Setting, ...Project] },
  {
    path: '/login',
    name: 'login',
    component: () => import('/@/views/login/index.vue'),
    meta: {
      title: 'commons.login',
      requiresAuth: false,
    },
  },
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
router.beforeEach((to, _from, next) => {
  const store = useAuthStore()
  const isAuthenticated = store.accessToken || ''
  if (to.path === '/login') {
    isAuthenticated ? next('/') : next()
  } else {
    if (!isAuthenticated) {
      store.$reset()
      next(`/login?redirect=${to.path}&params=${JSON.stringify(to.query ? to.query : to.params)}`)
    } else if (isAuthenticated && to.path === 'login') {
      next('/')
    } else {
      next()
    }
  }
})
router.afterEach((to) => {
  // TODO: title from sfc custom block?
  // const current = to.matched[to.matched.length - 1].components.default
  // const title = current.title ?? current.name
  const items = [import.meta.env.VITE_APP_TITLE]
  to.meta.title != null && items.unshift(i18n.t(to.meta.title as string))
  document.title = items.join(' | ')
})
export default router
