<template>
  <div class="w-[240px] bg-[#f8f8f9] h-[calc(100vh-60px)] p-[10px] border-r-[1px] border-[#eaebec]">
    <div class="flex flex-col gap-10px">
      <a-input class="arco-input-wrapper h-[40px] !rounded-[6px] !border-[#eaebec]" placeholder="搜索" size="large" allow-clear>
        <template #prefix>
          <icon-search />
        </template>
      </a-input>
      <div class="h-[1px] w-full bg-[#eaebec] my-[15px]" />
      <span class="text-[#8c8c8c]">最近使用</span>
      <div class="mt-[10px] truncate flex flex-col gap-[5px]">
        <template v-for="(item, index) in audioList" :key="index">
          <div class="p-[5px] flex items-center gap-[10px] rounded-[6px] cursor-pointer hover:bg-[#f1f1f3]" :class="[{ 'bg-[#edeaf7]': currentAudio?.id === item.id }]" @click="handelClick(item)">
            <img src="@/assets/file/voice.svg" alt="voice"/>
            <div class="flex flex-col">
              <span class="text-[#404040] leading-5 text-[13px] w-[180px] block truncate">{{ item?.fileName }}</span>
              <span class="text-[#8c8c8c] leading-5 text-[12px]">{{ hoursAgo(item?.createTime) }}</span>
            </div>
          </div>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { AudioFileControllerService } from '@/api'
import { type AudioFileVO } from '@/api'
import { useUserStore } from '@/store/user'
const userStore = useUserStore()
const audioList = ref([])
const currentAudio = ref<AudioFileVO>({})
onMounted(() => {
  getAudioList()
})
const handelClick = (item: AudioFileVO) => {
  currentAudio.value = item
}
const getAudioList = () => {
  const query = {
    userId: userStore.loginUser.id,
  }
  AudioFileControllerService.listAudioFileByPageUsingPost(query).then((res) => {
    if (res.code === 200) {
      audioList.value = res.data.records || []
    }
  })
}

function hoursAgo(createdAt: string | number | Date): string {
  const createdTime = new Date(createdAt).getTime()
  const now = Date.now()
  const diffMs = now - createdTime
  if (diffMs < 0) return '时间不合法'
  const diffHours = Math.floor(diffMs / (1000 * 60 * 60))
  if (diffHours === 0) {
    return '不到1小时前'
  }
  return `${diffHours}小时前`
}

defineExpose({
  getAudioList,
})
</script>

<style lang="scss" scoped></style>
