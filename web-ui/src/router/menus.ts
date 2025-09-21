import { ACCESS_ENUM } from '@/access/accessEnum'
import type { RouteRecordRaw } from 'vue-router'

export const menus: Array<RouteRecordRaw> = [
  {
    name: '题目列表',
    path: '/question/list',
    component: () => import('@/views/question/List.vue'),
  },
  {
    name: '题目管理',
    path: '/question/manage',
    component: () => import('@/views/question/Manage.vue'),
    meta: { access: ACCESS_ENUM.ADMIN },
  },
  {
    name: '题目详情',
    path: '/question/detail',
    component: () => import('@/views/question/Detail.vue'),
    meta: {},
  },
]
