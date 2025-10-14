<template>
  <div class="mt-[60px] mx-[auto] min-w-[840px]">
    <div class="flex flex-col items-center border-[1px] border-[#bdbdbd] border-dashed rounded-[20px] p-[32px] bg-[#fafafa]">
      <img class="max-h-[108px]" src="@/assets/file/upload.png" alt="upload" />
      <div class="p-[10px] flex justify-center items-center gap-[10px] mt-[12px] cursor-pointer bg-[#222226] hover:bg-[#000] rounded-[10px] min-w-[328px] h-[48px]" @click="startRecording">
        <img src="@/assets/file/start.svg" alt="start" />
        <span class="text-[#FFF]">开始录音</span>
      </div>
      <a-upload
        v-model="fileList"
        :on-before-upload="beforeUpload"
        :custom-request="handleUpload"
        class="flex flex-col !w-[328px]"
        :with-credentials="true"
        accept=".mp3,.mpga,.oga,.mogg,.aac,.webm,.opus,.flac,.wav,.m4a,.ogg"
        :file-list="fileList"
      >
        <template #upload-button>
          <div class="text-[#222226] p-[10px] flex justify-center items-center gap-[10px] mt-[12px] cursor-pointer bg-[#ececee] hover:bg-[#f5f5f5] rounded-[10px] min-w-[328px] h-[48px]">
            <icon-upload />
            <span>选择本地文件</span>
          </div>
        </template>
      </a-upload>
      <div class="mt-[20px] flex items-center gap-[10px]">
        <span class="text-[12px] text-[#8c8c8c]">最大文件大小：50M </span>
        <span class="text-[12px] text-[#8c8c8c]">最大录制时长：6 小时</span>
        <span class="text-[12px] text-[#8c8c8c]">支持的文件类型</span>
        <a-tooltip
          :mini="true"
          :arrow-style="{ display: 'none' }"
          content-class="!rounded-md !text-[#f3f3f3] !text-[12px]"
          background-color="!bg-[#252525b]"
          content="mp3, mpga, oga, mogg, aac, webm, opus, flac, wav, m4a, ogg"
          position="top"
        >
          <span class="text-[#8c8c8c] cursor-pointer">
            <icon-question-circle />
          </span>
        </a-tooltip>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { AudioFileControllerService } from '@/api'
import { Message } from '@arco-design/web-vue'
const allowTypes = [
  'audio/mpeg', // mp3, mpga
  'audio/ogg', // oga, ogg, mogg
  'audio/aac',
  'audio/webm',
  'audio/opus',
  'audio/flac',
  'audio/wav',
  'audio/x-m4a', // m4a
]
const fileList = ref([])

/**
 * 校验文件合法,文件类型,文件大小
 * @param file - File对象
 */
const beforeUpload = (file: File) => {
  if (!allowTypes.includes(file.type)) {
    Message.error('不支持该文件类型')
    return false
  }
  if (file.size > 50 * 1024 * 1024) {
    Message.error('文件大小超出限制')
    return false
  }
  return true
}

const emit = defineEmits(['uploadSuccess'])

const handleUpload = async ({ onError, onSuccess, fileItem }) => {
  // fileItem.file is already a File (inherits from Blob) with name/type
  const file: File = fileItem.file as File
  const res = await AudioFileControllerService.uploadUsingPost(file)
  if (res.code === 200) {
    onSuccess()
    Message.success('上传成功')
    emit('uploadSuccess', res.data)
  } else {
    onError()
    Message.error(res.message)
  }
}
const startRecording = () => {

}
</script>

<style scoped lang="scss"></style>
