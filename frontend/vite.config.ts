import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

const pathResolve = (src: string): string => {
  return path.resolve(__dirname, '.', src)
}
// https://vitejs.dev/config/
export default defineConfig(({ mode }) => {
  // 根据当前工作目录中的 `mode` 加载 .env 文件
  // 设置第三个参数为 '' 来加载所有环境变量，而不管是否有 `VITE_` 前缀。
  const env = loadEnv(mode, process.cwd(), '')
  return {
    // vite 配置
    plugins: [vue()],
    // 服务器选项
    server: {
      host: true,
      proxy: {
        // 带选项写法：http://localhost:5173/api/bar -> http://jsonplaceholder.typicode.com/bar
        [env.VITE_APP_BASE_API]: {
          target: env.VITE_APP_PROXY_URL,
          changeOrigin: true,
          rewrite: (path) => path.replace(new RegExp('^' + env.VITE_APP_BASE_API), ''),
        },
      },
    },
    resolve: {
      alias: [
        // /@/xxxx => src/xxxx
        {
          find: /\/@\//,
          replacement: pathResolve('src') + '/',
        },
      ],
    },
    define: {
      __APP_ENV__: JSON.stringify(env.APP_ENV),
      __APP_VERSION__: JSON.stringify('v0.0.1'),
    },
  }
})
