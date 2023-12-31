import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import router from './router'
import naive from './plugins/naive'
// 通用字体
import 'vfonts/Lato.css'
// 等宽字体
// import 'vfonts/FiraCode.css'

import pinia from './store'
import i18n from './i18n'
import permission from './directive/permission'
import debounce from './directive/debounce'

import 'virtual:uno.css'

const app = createApp(App)
app.use(router)
app.use(naive)
app.use(pinia)
app.use(i18n)
app.directive('permission', permission)
app.directive('debounce', debounce)
app.mount('#app')
