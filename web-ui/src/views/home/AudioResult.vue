<template>
  <div class="h-[calc(100vh-100px)]">
    <div class="flex h-[70px] items-center justify-center gap-6 bg-[#FFF] rounded-lg border border-[#eaebec]">
      <audio-player-card class="p-2 px-5" :src="minioUrl + props.file?.filePath" ref="audioPlayerCardRef" />
    </div>
    <div class="h-[calc(100vh-170px)] overflow-y-auto mt-3 flex flex-col gap-[10px]">
      <template v-for="item in resultList" :key="item?.id">
        <template v-for="sig in item.resultSegments" :key="sig?.id">
          <div class="flex flex-col text-[#262626]" @click="handelClick(sig)">
            <div class="flex flex-col gap-[10px] p-[10px] cursor-pointer bg-[#FFF] rounded-[6px] border border-[#eaebec] hover:bg-[#f8f8f9]" :class="[sig?.id === currentPartId ? '!bg-[#f1f2f3]' : '']">
              <div class="flex gap-[10px]">
                <icon-user />
                <span>{{ sig?.speaker }}</span>
                <span class="text-[#333333]">{{ `${formatTime(sig?.start)} - ${formatTime(sig?.end)}` }}</span>
                <span class="text-[#8C8C8C]">{{ formatTime(sig?.end - sig?.start) }}</span>
              </div>
              <span class="text-[#333333]">
                {{ sig?.text }}
              </span>
            </div>
          </div>
        </template>
      </template>
    </div>
  </div>
</template>
<script setup lang="ts">
import { AudioFileVO, type TranscriptSegment } from '@/api'
import { onMounted, ref, watch } from 'vue'
import { TranscribeResultControllerService } from '@/api'
import { type TranscribeResultVO } from '@/api'
import AudioPlayerCard from '@/components/AudioPlayer/AudioPlayerCard.vue'
const audioPlayerCardRef = ref(null)
const minioUrl = window._properties.minioUrl
const currentPartId = ref<string>()
const props = defineProps<{
  file?: AudioFileVO
}>()
const resultList = ref<Array<TranscribeResultVO>>([])
onMounted(() => {
  getResult(props.file?.id)
})

watch(
  () => props.file,
  (newVal) => {
    if (newVal?.id) {
      getResult(newVal.id)
    }
  },
)
const formatTime = (sec: number) => {
  const m = Math.floor(sec / 60)
  const s = Math.floor(sec % 60)
  return `${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`
}
const getResult = async (id: number) => {
  const res = await TranscribeResultControllerService.listUsingGet(id)
  resultList.value = res.data
  console.log(resultList.value)
}
const handelClick = (sig: TranscriptSegment) => {
  currentPartId.value = sig.id
  audioPlayerCardRef.value.setCurrentTime(sig.start)
}
</script>

<style scoped lang="scss"></style>
