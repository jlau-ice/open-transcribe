<template>
  <div class="mt-[60px] mx-[auto] min-w-[840px]">
    <div class="flex flex-col items-center border-t border-l border-r border-[#bdbdbd] border-dashed rounded-t-[20px] p-[32px] bg-[#fafafa]">
      <div class="flex justify-center items-center p-[10px] h-[80px] w-[80px] rounded-[10px] bg-[linear-gradient(180deg,#adb2be,#d5d6d8)]">
        <img src="@/assets/file/audio.svg" alt="audio" />
      </div>
      <div class="mt-3">
        <span class="text-[#8C8C8C] text-[16px]">音频时长：</span>
        <span class="text-[#262626] text-[18px] font-bold">{{ formatTime(audioDuration) }}</span>
      </div>
      <span class="pt-3 pb-5 text-[13px] text-[#8C8C8C]">开始转录后，转录过程无法取消。请确保需要转录的内容后再开始操作。</span>
      <button
        class="flex items-center justify-center h-[48px] w-[328px] rounded-[8px] gap-[10px] text-[#FFF] bg-[#222226] hover:bg-[#000000] disabled:bg-[#4B4B4B] disabled:text-gray-200 disabled:cursor-not-allowed"
        :disabled="props.file?.status === 1 || props.file?.status === 2"
        @click="startTranscription"
      >
        <img src="@/assets/file/transcribe.svg" alt="transcribe" />
        <span v-if="props.file?.status === 3">重新转录</span>
        <span v-else>开始转录</span>
      </button>
    </div>
    <!-- <div class="h-1 w-full bg-[#eaebec]" /> -->
    <div class="flex items-center justify-between border-b border-l border-r border-[#bdbdbd] border-dashed rounded-b-[20px] px-5 py-2 bg-[#fafafa]">
      <audio-player :src="minioUrl + props.file?.filePath" @get-audio-duration="getAudioDuration" />
      <!-- <div class="text-[12px]">
        <span class="text-[#262626]">00:00</span>
        <span class="text-[#8c8c8c]">/00:09</span>
      </div>
      <div class="flex items-center h-[44px] gap-3">
        <span class="cursor-pointer"><img src="@/assets/file/l15.svg" alt="l15" /></span>
        <span class="cursor-pointer"><img src="@/assets/file/play.svg" alt="play" /></span>
        <span class="cursor-pointer"><img src="@/assets/file/r15.svg" alt="r15" /></span>
      </div>
      <div class="flex items-center gap-6">
        <div class="cursor-pointer text-[12px]">1.0X</div>
        <span class="cursor-pointer"><img src="@/assets/file/down.svg" alt="down" /></span>
      </div> -->
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { AudioFileVO } from '@/api'
import AudioPlayer from '@/components/AudioPlayer/index.vue'
const props = defineProps<{
  file?: AudioFileVO
}>()
const minioUrl = window._properties.minioUrl
const emit = defineEmits(['transcription'])
const startTranscription = () => {
  emit('transcription', props.file)
}
const audioDuration = ref(0)
const getAudioDuration = (duration: number) => {
  audioDuration.value = duration
}

const formatTime = (duration: number) => {
  const m = Math.floor(duration / 60)
  const s = Math.floor(duration % 60)
  return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`
}
</script>

<style scoped lang="scss"></style>
