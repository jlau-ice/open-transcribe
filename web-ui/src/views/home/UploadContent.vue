<template>
  <div class="py-20 px-6 w-full">
    <div class="flex flex-col items-center">
      <h1 class="text-[#262626] text-[48px] font-bold leading-tight m-0">免费音频转文本</h1>
      <span class="text-[#8c8c8c] text-[18px] leading-tigh mt-[8px]">使用我们的免费音频转文本工具，即刻将音频转为文本。完美支持语音转文本、声音转文本以及AI驱动的转录工具。</span>
      <div>
        <audio-upload v-if="!uploadFile" @upload-success="uploadSuccess" />
        <transcription v-else/>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import AudioUpload from '@/views/home/AudioUpload.vue'
import { FileItem } from '@arco-design/web-vue'
import Transcription from "@/views/home/Transcription.vue";
const emit = defineEmits(['upload-success'])
const uploadFile = ref<FileItem>(null)
const uploadSuccess = (fileItem: FileItem) => {
  uploadFile.value = fileItem
  emit('upload-success')
}
</script>
<style lang="scss" scoped>
:deep() {
  .arco-upload-list-item.arco-upload-list-item-done {
    margin-top: 10px !important;
  }
}
</style>
