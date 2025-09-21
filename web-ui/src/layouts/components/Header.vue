<template>
  <div class="h-[60px] overflow-hidden border-b border-b-[#e0e0e0] border-solid">
    <div class="flex items-center">
      <div class="flex items-center gap-[10px] ml-[34px] cursor-pointer select-none">
        <img width="35px" src="@/assets/logo-png.png" alt="logo" />
      </div>
      <a-menu mode="horizontal" :default-selected-keys="[currentPath]" @menu-item-click="menuItemClick">
        <a-menu-item v-for="value in filterMenu" :key="value.path">
          <span class="font-bold text-[15px]">{{ value.name }}</span>
        </a-menu-item>
      </a-menu>
      <a-dropdown trigger="hover">
        <div class="mr-[34px] flex items-center gap-[10px] cursor-pointer select-none">
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
