<template>
  <div class="py-20 px-6 w-full">
    <div class="flex flex-col items-center">
      <h1 class="text-[#262626] text-[48px] font-bold leading-tight m-0">免费音频转文本</h1>
      <span class="text-[#8c8c8c] text-[18px] leading-tigh mt-[8px]">使用我们的免费音频转文本工具，即刻将音频转为文本。完美支持语音转文本、声音转文本以及AI驱动的转录工具。</span>
      <div class="mt-[60px] mx-[auto] min-w-[840px]">
        <div class="flex flex-col items-center border-[1px] border-[#bdbdbd] border-dashed rounded-[20px] p-[32px] bg-[#fafafa]">
          <img class="max-h-[108px]" src="@/assets/file/upload.png" />
          <div class="p-[10px] flex justify-center items-center gap-[10px] mt-[12px] cursor-pointer bg-[#222226] hover:bg-[#000] rounded-[10px] min-w-[328px] h-[48px]">
            <svg fill="none" style="min-width: 20px; min-height: 20px" width="20" height="20" viewBox="0 0 20 20">
              <g id="Record 1">
                <path
                  id="Vector"
                  opacity="0.1"
                  d="M18.1389 10C18.1389 11.607 17.6623 13.1779 16.7695 14.514C15.8768 15.8502 14.6078 16.8916 13.1232 17.5065C11.6385 18.1215 10.0048 18.2824 8.42875 17.9689C6.85265 17.6554 5.40492 16.8815 4.26862 15.7452C3.13231 14.6089 2.35848 13.1612 2.04498 11.5851C1.73147 10.009 1.89238 8.37535 2.50734 6.8907C3.1223 5.40605 4.1637 4.1371 5.49985 3.24431C6.836 2.35152 8.40688 1.875 10.0139 1.875C12.1687 1.875 14.2354 2.73102 15.7591 4.25476C17.2828 5.77849 18.1389 7.84512 18.1389 10Z"
                  fill="#C50B17"
                ></path>
                <path
                  id="Vector_2"
                  d="M10.0139 1.25C8.28327 1.25 6.59155 1.76318 5.15262 2.72464C3.71369 3.6861 2.59218 5.05267 1.92991 6.65152C1.26765 8.25037 1.09437 10.0097 1.43199 11.707C1.76961 13.4044 2.60297 14.9635 3.82667 16.1872C5.05038 17.4109 6.60948 18.2442 8.30682 18.5819C10.0042 18.9195 11.7635 18.7462 13.3623 18.0839C14.9612 17.4217 16.3278 16.3002 17.2892 14.8612C18.2507 13.4223 18.7639 11.7306 18.7639 10C18.7612 7.68017 17.8385 5.45611 16.1981 3.81574C14.5578 2.17537 12.3337 1.25265 10.0139 1.25ZM10.0139 17.5C8.5305 17.5 7.08045 17.0601 5.84708 16.236C4.61371 15.4119 3.65242 14.2406 3.08476 12.8701C2.51711 11.4997 2.36858 9.99168 2.65797 8.53682C2.94736 7.08197 3.66166 5.74559 4.71056 4.6967C5.75945 3.64781 7.09582 2.9335 8.55068 2.64411C10.0055 2.35472 11.5135 2.50325 12.884 3.0709C14.2544 3.63856 15.4258 4.59985 16.2499 5.83322C17.074 7.06659 17.5139 8.51664 17.5139 10C17.5119 11.9885 16.7211 13.895 15.315 15.3011C13.9089 16.7072 12.0024 17.498 10.0139 17.5Z"
                  fill="#FA494C"
                ></path>
                <path
                  id="Vector_3"
                  d="M15.01 10C15.01 10.9889 14.7167 11.9556 14.1673 12.7779C13.6179 13.6001 12.837 14.241 11.9234 14.6194C11.0097 14.9978 10.0044 15.0969 9.0345 14.9039C8.06459 14.711 7.17368 14.2348 6.47442 13.5355C5.77515 12.8363 5.29895 11.9454 5.10602 10.9755C4.9131 10.0055 5.01212 9.00021 5.39055 8.08658C5.76899 7.17295 6.40985 6.39206 7.2321 5.84265C8.05435 5.29324 9.02105 5 10.01 5C11.336 5 12.6078 5.52678 13.5455 6.46447C14.4832 7.40215 15.01 8.67392 15.01 10Z"
                  fill="#FA494C"
                ></path>
              </g>
            </svg>
            <span class="text-[#FFF]">开始录音</span>
          </div>
          <a-upload v-model="fileList" :on-before-upload="beforeUpload" :custom-request="handleUpload" class="flex flex-col !w-[328px]" :with-credentials="true" accept=".mp3,.mpga,.oga,.mogg,.aac,.webm,.opus,.flac,.wav,.m4a,.ogg" :file-list="fileList">
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
const beforeUpload = (file: File ) => {
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
const handleUpload = async ({ onProgress, onError, onSuccess, fileItem }) => {
  // fileItem.file is already a File (inherits from Blob) with name/type
  const file: File = fileItem.file as File
  await AudioFileControllerService.uploadUsingPost(file)
    .then((res) => {
      onSuccess && onSuccess(res)
    })
    .catch((err) => {
      onError && onError(err)
    })
}
</script>

<style lang="scss" scoped>
:deep() {
  .arco-upload-list-item.arco-upload-list-item-done {
    margin-top: 10px !important;
  }
}
</style>
