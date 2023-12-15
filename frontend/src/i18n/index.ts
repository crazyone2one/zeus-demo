import en_US from './locales/en_US'
import zh_CN from './locales/zh_CN'
import { createI18n } from 'vue-i18n'
import { useSettingStore } from '../store/modules/setting-store'
import pinia from '../store'

const getLocale = (): string => {
  const store = useSettingStore(pinia)
  return store.language || 'zh-cn'
}
const instance = createI18n({
  // something vue-i18n options here ...
  legacy: false,
  globalInjection: true,
  messages: {
    ['zh-cn']: {
      ...zh_CN,
    },
    ['en']: {
      ...en_US,
    },
  },
  locale: getLocale(), // set locale
  fallbackLocale: 'en', // set fallback locale
})

export const i18n = instance.global
export default instance
