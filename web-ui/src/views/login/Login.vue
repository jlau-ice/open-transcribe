<template>
  <div class="login-container flex flex-col items-center justify-between">
    <div class="py-[20px] flex justify-center items-center h-[calc(60vh-80px)]">
      <div class="flex flex-col justify-center items-center gap-[15px] select-none">
        <div class="flex items-center gap-[10px]">
          <img width="44" src="@/assets/logo-png.png" alt="logo" />
          <span class="text-[33px] font-semibold font-family-alibaba">Open Transcribe</span>
        </div>
        <span class="text-[#515258] tracking-wider font-family-alibaba">开源的语音转文本平台</span>
        <a-form :model="form" :rules="rules" layout="vertical" ref="formRef" class="mt-[20px]" @submit="handleLogin">
          <a-form-item hide-label field="userAccount">
            <a-input v-model="form.userAccount" placeholder="请输入账号" allow-clear />
          </a-form-item>
          <a-form-item hide-label field="userPassword">
            <a-input-password v-model="form.userPassword" v-model:visibility="visibility" placeholder="请输入密码" :defaultVisibility="false" allow-clear />
          </a-form-item>
          <div class="text-[#bbb] text-right mb-[10px]">
            没有账号？
            <span class="cursor-pointer hover:text-[#1677ff]" @click="toRegister"> 去注册 </span>
          </div>
          <a-form-item hide-label>
            <a-button html-type="submit" type="primary" class="w-full"> 登录 </a-button>
          </a-form-item>
        </a-form>
      </div>
    </div>
    <div class="py-[20px]"></div>
  </div>
</template>

<script setup lang="ts">
import router from '@/router'
import { reactive, ref } from 'vue'
import type { UserLoginRequest } from '@/api'
import { UserControllerService } from '@/api'
import type { FormInstance } from '@arco-design/web-vue'
import { Message } from '@arco-design/web-vue'
import { useUserStore } from '@/store/user'
const userStore = useUserStore()

const form = reactive<UserLoginRequest>({
  userAccount: '',
  userPassword: '',
})
const rules = ref({
  userAccount: [{ required: true, message: '请输入账号' }],
  userPassword: [
    { required: true, message: '请输入密码' },
    { minLength: 6, message: '密码至少6位' },
  ],
})
const formRef = ref<FormInstance>()
const visibility = ref(true)
const handleLogin = async () => {
  try {
    const isValid = await formRef.value.validate()
    if (isValid) return
    const res = await UserControllerService.userLoginUsingPost(form)
    if (res.code === 200) {
      Message.success('登录成功')
      await userStore.fetchLoginUser()
      await router.push({ path: '/home' })
    } else {
      Message.error(res.message)
    }
  } catch (err) {
    console.log('表单校验失败:', err)
  }
  console.log('登录中...', form)
}
const toRegister = () => {
  router.push({ path: '/register' })
}
</script>

<style scoped lang="scss">
.login-container {
  width: 100vw;
  height: 100vh;
  background-image: url('@/assets/background.png');
  background-repeat: no-repeat;
  background-attachment: fixed;
  background-position: center;
  background-size: cover;
}

.font-family-alibaba {
  font-family: AlibabaSans, sans-serif;
}
</style>
