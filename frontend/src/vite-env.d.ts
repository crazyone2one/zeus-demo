declare const __APP_ENV__: string
declare const __APP_VERSION__: string
declare module '*.vue' {
  import type { defineComponent } from 'vue'
  import type { MessageProviderInst, DialogProviderInst, NotificationProviderInst } from 'naive-ui'
  // 增加全局类型
  global {
    interface Window {
      $message: MessageProviderInst
      $dialog: DialogProviderInst
      $notification: NotificationProviderInst
    }
  }
  const component: ReturnType<typeof defineComponent>
  export default component
}
