<template>
  <div class="h-[60px] shadow overflow-hidden">
    <div class="flex items-center">
      <div class="flex items-center gap-[10px] ml-[24px] w-[110px] cursor-pointer select-none">
        <img width="40px" src="@/assets/leetcode.svg" alt="logo" />
        <span class="text-[#3D3D3D] font-bold whitespace-nowrap">ICE OJ</span>
      </div>
      <a-menu mode="horizontal" :default-selected-keys="[currentPath]" @menu-item-click="menuItemClick">
        <a-menu-item v-for="value in filterMenu" :key="value.path">
          {{ value.name }}
        </a-menu-item>
      </a-menu>
      <a-dropdown trigger="hover">
        <div class="mr-[24px] flex items-center gap-[10px] cursor-pointer select-none">
          <a-avatar>
            <img width="40" height="40" alt="avatar" src="https://p1-arco.byteimg.com/tos-cn-i-uwbnlip3yd/3ee5f13fb09879ecb5185e440cef6eb9.png~tplv-uwbnlip3yd-webp.webp" />
          </a-avatar>
          <span class="max-w-[60px] truncate text-[#3d3d3d]">{{ userStore?.loginUser?.userName }}</span>
        </div>
        <template #content>
          <a-doption @click="logout">{{ userStore?.loginUser?.userRole === ACCESS_ENUM.NO_LOGIN ? '去登录' : '退出登录' }}</a-doption>
        </template>
      </a-dropdown>
    </div>
  </div>
</template>
<script lang="ts" setup>
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/store/user'
import checkAccess from '@/access/checkAccess'
import { Modal, Message } from '@arco-design/web-vue'
import { ACCESS_ENUM } from '@/access/accessEnum'
const userStore = useUserStore()
const router = useRouter()
const route = useRoute()
import { menus } from '@/router/menus'
import { computed } from 'vue'
const currentPath = computed(() => route.path)
const menuItemClick = (e: string) => {
  router.push({ path: e })
}

const filterMenu = computed(() => {
  return menus.filter((item) => {
    return checkAccess(userStore.loginUser, item?.meta?.access as string)
  })
})

const logout = () => {
  if (userStore.loginUser.userRole === ACCESS_ENUM.NO_LOGIN) {
    router.push({ path: '/login' })
    return
  }
  Modal.confirm({
    title: '提示',
    content: '确定退出系统吗？',
    titleAlign: 'start',
    okText: '确定',
    cancelText: '取消',
    closable: true,
    hideCancel: false,
    modalClass: 'modal-class',
    onOk: async () => {
      await userStore.fetchLogoutUser()
      Message.success('退出成功')
    },
    onCancel: () => {
      Message.info('已取消')
    },
  })
}
</script>
<style lang="scss" scoped>
:deep() {
  .arco-menu-inner {
    padding-left: 0;
  }
}
:global(.modal-class) {
  padding: 15px !important;
}
:global(.arco-modal-header) {
  margin-bottom: 10px !important;
}
:global(.arco-modal-footer) {
  margin-top: 10px !important;
}
</style>
