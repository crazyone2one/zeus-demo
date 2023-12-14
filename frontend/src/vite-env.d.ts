declare const __APP_ENV__: string
declare const __APP_VERSION__: string
declare module '*.vue' {
  import type { DefineComponent } from 'vue'
  import type { MessageProviderInst, DialogProviderInst, NotificationProviderInst } from 'naive-ui'
  // 增加全局类型
  global {
    interface Window {
      $message: MessageProviderInst
      $dialog: DialogProviderInst
      $notification: NotificationProviderInst
    }
  }
  // eslint-disable-next-line @typescript-eslint/no-explicit-any, @typescript-eslint/ban-types
  const component: DefineComponent<{}, {}, any>
  export default component
}
