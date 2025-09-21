import type { RouteRecordRaw } from 'vue-router'
import { menus } from './menus'

export const routes: Array<RouteRecordRaw> = [
  {
    path: '',
    name: 'home',
    redirect: '/home',
    component: () => import('@/layouts/index.vue'),
    children: [...menus],
  },
  {
    name: 'noAccess',
    path: '/noAccess',
    component: () => import('@/views/result/403.vue'),
  },
  {
    name: 'notFound',
    path: '/:pathMatch(.*)*',
    component: () => import('@/views/result/404.vue'),
  },
  {
    name: 'login',
    path: '/login',
    component: () => import('@/views/login/Login.vue'),
  },
  {
    name: 'register',
    path: '/register',
    component: () => import('@/views/login/Register.vue'),
  },
]
