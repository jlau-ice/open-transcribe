<template>
  <div class="login-container flex flex-col items-center justify-between">
    <div class="py-[20px] flex justify-center items-center h-[calc(60vh-80px)]">
      <div class="flex flex-col justify-center items-center gap-[15px] select-none">
        <div class="flex items-center gap-[10px]">
          <img width="44" src="@/assets/logo-png.png" alt="logo" />
          <span class="text-[33px] font-semibold font-family-alibaba">Open Transcribe</span>
        </div>
        <span class="text-[#515258] tracking-wider font-family-alibaba">开源的语音转文本平台</span>
        <a-form :model="form" :rules="rules" layout="vertical" class="mt-[20px]" ref="formRef" @submit="handleRegister">
          <a-form-item hide-label field="userAccount">
            <a-input v-model="form.userAccount" placeholder="请输入账号" allow-clear />
          </a-form-item>
          <a-form-item hide-label field="userPassword">
            <a-input-password v-model="form.userPassword" v-model:visibility="visibility" placeholder="请输入密码" :defaultVisibility="false" allow-clear />
          </a-form-item>
          <a-form-item hide-label field="checkPassword">
            <a-input-password v-model="form.checkPassword" v-model:visibility="visibility" placeholder="请输入确认密码" :defaultVisibility="false" allow-clear />
          </a-form-item>
          <div class="text-[#bbb] text-right mb-[10px]">
            有账号？
            <span class="cursor-pointer hover:text-[#1677ff]" @click="toLogin">去登录</span>
          </div>
          <a-form-item hide-label>
            <a-button html-type="submit" type="primary" class="w-full"> 立即注册 </a-button>
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
import type { UserRegisterRequest } from '@/api'
import { UserControllerService } from '@/api'
import type { FormInstance } from '@arco-design/web-vue'
import message from '@arco-design/web-vue/es/message'
const form = reactive<UserRegisterRequest>({
  userAccount: '',
  userPassword: '',
  checkPassword: '',
})
const formRef = ref<FormInstance>()
const visibility = ref(true)
const rules = {
  userAccount: [{ required: true, message: '请输入账号' }],
  userPassword: [
    { required: true, message: '请输入密码' },
    { minLength: 6, message: '密码至少6位' },
  ],
  checkPassword: [
    { required: true, message: '请输入密码' },
    { minLength: 6, message: '密码至少6位' },
  ],
}
const handleRegister = async () => {
  try {
    const isValid = await formRef.value.validate()
    if (isValid) return
    UserControllerService.userRegisterUsingPost(form).then((res) => {
      if (res.code === 200) {
        message.success('注册成功')
        router.push({ path: '/login' })
      } else {
        message.error(res.message)
      }
    })
  } catch (err) {
    console.log('表单校验失败:', err)
  }
}
const toLogin = () => {
  router.push({ path: '/login' })
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
