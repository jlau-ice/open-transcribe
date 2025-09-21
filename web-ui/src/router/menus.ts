import { ACCESS_ENUM } from '@/access/accessEnum'
import type { RouteRecordRaw } from 'vue-router'

export const menus: Array<RouteRecordRaw> = [
  {
    name: '文件管理',
    path: '/file/list',
    component: () => import('@/views/file/FileList.vue'),
  },
  {
    name: '任务管理',
    path: '/task/manage',
    component: () => import('@/views/task/TaskManage.vue'),
    meta: { access: ACCESS_ENUM.ADMIN },
  },
  {
    name: '模型配置',
    path: '/model/config',
    component: () => import('@/views/model/ModelConfig.vue'),
    meta: {},
  },
]
