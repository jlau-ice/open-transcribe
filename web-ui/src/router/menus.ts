import { ACCESS_ENUM } from '@/access/accessEnum'
import type { RouteRecordRaw } from 'vue-router'

export const menus: Array<RouteRecordRaw> = [
  {
    name: '首页',
    path: '/home',
    component: () => import('@/views/home/Home.vue'),
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
