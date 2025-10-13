import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'
import vueDevTools from 'vite-plugin-vue-devtools'
export default defineConfig({
  plugins: [vue(), vueDevTools()],
  server: {
    host: true,
    port: 8888,
    proxy: {
      '/api': 'http://127.0.0.1:8101',
      // '/asr': 'http://127.0.0.01:9000',
      '/asr': 'http://192.168.172.214:9998',
    },
  },
  resolve: {
    extensions: ['.mjs', '.js', '.ts', '.jsx', '.tsx', '.json', '.vue'],
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
})
