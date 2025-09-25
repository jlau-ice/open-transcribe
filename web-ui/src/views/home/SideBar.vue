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
            <svg fill="none" width="20" height="20" viewBox="0 0 20 20" style="min-width: 20px; min-height: 20px">
              <rect width="20" height="20" rx="3.33333" fill="url(#paint0_linear_112_9397_:r45l:)"></rect>
              <path
                d="M4.81601 7.48454C4.8912 7.28282 5.17651 7.28282 5.2517 7.48454L5.75187 8.82642C5.83041 9.03714 5.99663 9.20336 6.20736 9.2819L7.54923 9.78207C7.75095 9.85726 7.75095 10.1426 7.54923 10.2178L6.20736 10.7179C5.99663 10.7965 5.83042 10.9627 5.75187 11.1734L5.2517 12.5153C5.17651 12.717 4.8912 12.717 4.81601 12.5153L4.31584 11.1734C4.23729 10.9627 4.07108 10.7965 3.86035 10.7179L2.51848 10.2178C2.31676 10.1426 2.31676 9.85726 2.51847 9.78207L3.86035 9.2819C4.07108 9.20336 4.23729 9.03714 4.31584 8.82642L4.81601 7.48454Z"
                fill="white"
              ></path>
              <path
                d="M10.5894 4.19458L10.5894 16.4724M7.70048 7.08347L7.70052 7.80569M7.70048 12.8612L7.70052 13.5835M13.4783 7.08347L13.4783 13.5835M16.3672 9.61125L16.3672 11.0557"
                stroke="white"
                stroke-width="1.2"
                stroke-linecap="round"
              ></path>
              <defs>
                <linearGradient id="paint0_linear_112_9397_:r45l:" x1="10" y1="20" x2="10" y2="0" gradientUnits="userSpaceOnUse">
                  <stop stop-color="#57BFFF"></stop>
                  <stop offset="1" stop-color="#2B90FF"></stop>
                </linearGradient>
              </defs>
            </svg>
            <div class="flex flex-col">
              <span class="text-[#404040] leading-5 text-[12px] w-[180px] block truncate">{{ item?.fileName }}</span>
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
