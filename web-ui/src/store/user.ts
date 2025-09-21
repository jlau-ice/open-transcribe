import { defineStore } from 'pinia'
import { ref } from 'vue'
import { type LoginUserVO, UserControllerService } from '@/api'
import { ACCESS_ENUM } from '@/access/accessEnum'
import router from '@/router'

export const useUserStore = defineStore('user', () => {
  const loginUser = ref<LoginUserVO>({
    userName: '未登录',
    userRole: ACCESS_ENUM.NO_LOGIN,
  })
  async function fetchLoginUser() {
    try {
      const res = await UserControllerService.getLoginUserUsingGet()
      if (res.code === 200) {
        loginUser.value = res.data || {}
      } else {
        loginUser.value = {
          userName: '未登录',
          userRole: ACCESS_ENUM.NO_LOGIN,
        }
      }
    } catch (error) {
      loginUser.value = {
        userName: '未登录',
        userRole: ACCESS_ENUM.NO_LOGIN,
      }
    }
  }
  async function fetchLogoutUser() {
    const res = await UserControllerService.userLogoutUsingPost()
    if (res.code === 200) {
      loginUser.value = {
        userName: '未登录',
        userRole: ACCESS_ENUM.NO_LOGIN,
      }
    }
    await router.push('/login')
  }
  return { loginUser, fetchLoginUser,fetchLogoutUser }
})
