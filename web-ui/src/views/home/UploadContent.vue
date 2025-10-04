<template>
  <div v-if="currentAudio?.status === 2" class="p-5 w-full">
    <audio-result :file="currentAudio" />
  </div>
  <div v-else class="py-20 px-6 w-full">
    <div class="flex flex-col items-center">
      <h1 class="text-[#262626] text-[48px] font-bold leading-tight m-0">免费音频转文本</h1>
      <span class="text-[#8c8c8c] text-[18px] leading-tight mt-[8px]">使用我们的免费音频转文本工具，即刻将音频转为文本。完美支持语音转文本、声音转文本以及AI驱动的转录工具。</span>
      <div>
        <audio-upload v-if="!currentAudio" @upload-success="uploadSuccess" />
        <transcription v-else :file="currentAudio" @transcription="starTranscription" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import AudioUpload from '@/views/home/AudioUpload.vue'
import { AudioFileControllerService } from '@/api'
// import { FileItem } from '@arco-design/web-vue'
import Transcription from '@/views/home/Transcription.vue'
import AudioResult from '@/views/home/AudioResult.vue'
import { AudioFileVO } from '@/api'
import { Message } from '@arco-design/web-vue'
const emit = defineEmits(['upload-success', 'star-transcription'])
const currentAudio = ref<AudioFileVO>(null)
const uploadSuccess = (file: AudioFileVO) => {
  currentAudio.value = file
  emit('upload-success', file)
}
const setCurrentAudio = (data: AudioFileVO) => {
  currentAudio.value = data
}

const starTranscription = async (data: AudioFileVO) => {
  // 开始转录任务
  const res = await AudioFileControllerService.transcribeUsingPost(data.id)
  if (res.code === 200) {
    Message.success('开始转录任务')
    // 刷新列表
    currentAudio.value.status = 1
  }
  // emit('star-transcription', data)
  console.log(data)
}

defineExpose({
  setCurrentAudio,
})
</script>
<style lang="scss" scoped>
:deep() {
  .arco-upload-list-item.arco-upload-list-item-done {
    margin-top: 10px !important;
  }
}
</style>
