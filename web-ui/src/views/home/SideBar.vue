<template>
  <div class="w-[240px] shrink-0 bg-[#f8f8f9] h-[calc(100vh-60px)] p-[10px] border-r-[1px] border-[#eaebec] max-h-[calc(100vh-60px)]">
    <div class="flex flex-col gap-10px">
      <div class="h-[140px]">
        <a-input class="arco-input-wrapper h-[40px] !rounded-[6px] !border-[#eaebec]" v-model="filterContent" placeholder="搜索" size="large" allow-clear>
          <template #prefix>
            <icon-search />
          </template>
        </a-input>
        <div class="text-[#8c8c8c] cursor-pointer hover:bg-[#f2f3f5] p-2.5 mt-2 border-solid border-[1px] rounded-[6px] border-[#eaebec]" @click="toUpload">
          <icon-upload />
          上传
        </div>
        <div class="h-[1px] w-full bg-[#eaebec] my-[15px]" />
        <span class="text-[#8c8c8c]">最近使用</span>
      </div>
      <div class="mt-[10px] max-h-[calc(100vh-220px)] !overflow-y-auto scrollbar-hide truncate flex flex-col gap-[5px]">
        <template v-for="(item, index) in filterAudioList" :key="index">
          <div
            class="relative p-[5px] flex items-center gap-[10px] rounded-[6px] cursor-pointer hover:bg-[#f1f1f3]"
            :class="{ 'bg-[#edeaf7]': currentAudio?.id === item.id }"
            @click="handelClick(item)"
            @mouseenter="item.hover = true"
            @mouseleave="item.hover = false"
          >
            <img src="@/assets/file/voice.svg" alt="voice" />
            <div class="flex flex-col">
              <span class="text-[#404040] leading-5 text-[13px] w-[120px] block truncate">{{ item?.fileName }}</span>
              <div>
                <span class="text-[#8c8c8c] leading-5 text-[12px]">{{ hoursAgo(item?.createTime) }}</span>
                <a-tag :color="statusColorMap[item?.status]" size="small" class="ml-2">
                  {{ statusMap[item?.status] || '未知状态' }}
                </a-tag>
              </div>
            </div>
            <div
              class="absolute right-2 top-1/2 -translate-y-1/2 flex gap-2 transition-opacity duration-200 text-[#83838f] hover:text-[#f53f3f]"
              :class="{ 'opacity-100': item.hover, 'opacity-0': !item.hover }"
              @click.stop="deleteAudio(item)"
            >
              <icon-delete />
            </div>
            <div
              class="absolute right-6 top-1/2 -translate-y-1/2 flex gap-2 transition-opacity duration-200 text-[#83838f] hover:text-[#56beff]"
              :class="{ 'opacity-100': item.hover, 'opacity-0': !item.hover }"
              @click.stop="openRename(item)"
            >
              <icon-edit />
            </div>
          </div>
        </template>
      </div>
    </div>
  </div>
  <a-modal v-model:visible="visible" simple @cancel="handleCancel" modal-class="!p-[15px]" @ok="rename">
    <div>
      <div class="mb-[10px]">请输入文件名</div>
      <a-input v-model="audioName" />
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { AudioFileControllerService } from '@/api'
import { type AudioFileVO } from '@/api'
import { useUserStore } from '@/store/user'
import { Message, Modal } from '@arco-design/web-vue'
const userStore = useUserStore()
import { getWSClient } from '@/utils/websocket'
import { type SuccessNotification } from '@/types/websocket'
const wsClient = getWSClient()
const audioList = ref<Array<AudioFileVO>>([])
const visible = ref(false)
const audioName = ref('')
const filterAudioList = computed(() => {
  return audioList.value.filter((item) => item.fileName.includes(filterContent.value || ''))
})
const currentAudio = ref<AudioFileVO>({})
const filterContent = ref<string>()
onMounted(() => {
  getAudioList()
})
wsClient.addListener((msg) => {
  const data: SuccessNotification = JSON.parse(msg)
  if (data.status === 'success') {
    getAudioList()
    Message.success(data.message || '音频处理成功')
  }
})

const statusMap = {
  0: '待处理',
  1: '处理中',
  2: '已完成',
  3: '处理失败',
}

const statusColorMap = {
  0: 'gray',
  1: 'blue',
  2: 'green',
  3: 'red',
}

const emit = defineEmits(['select-audio'])
const handelClick = (item: AudioFileVO) => {
  currentAudio.value = item
  emit('select-audio', item)
}
const getAudioList = async () => {
  const query = {
    userId: userStore.loginUser.id,
  }
  const res = await AudioFileControllerService.listAudioFileUsingPost(query)
  if (res.code === 200) {
    audioList.value = res.data || []
  }
}

function hoursAgo(createdAt: string | number | Date): string {
  const createdTime = new Date(createdAt).getTime()
  const now = Date.now()
  const diffMs = now - createdTime
  if (isNaN(createdTime)) return '时间不合法'
  if (diffMs < 0) return '时间不合法'
  const diffHours = Math.floor(diffMs / (1000 * 60 * 60))
  if (diffHours < 1) {
    return '不到1小时前'
  } else if (diffHours < 24) {
    return `${diffHours}小时前`
  } else {
    const diffDays = Math.floor(diffHours / 24)
    return `${diffDays}天前`
  }
}

const toUpload = () => {
  emit('select-audio', null)
}

const deleteAudio = async (item: AudioFileVO) => {
  Modal.confirm({
    title: '提示',
    content: '确定要删除该音频吗？',
    titleAlign: 'start',
    okText: '确定',
    cancelText: '取消',
    closable: true,
    hideCancel: false,
    modalClass: 'modal-class',
    onOk: async () => {
      const res = await AudioFileControllerService.deleteAudioFileUsingDelete(item.id)
      if (res.code === 200) {
        Message.success('删除成功')
        await getAudioList()
      }
    },
    onCancel: () => {
      Message.info('已取消')
    },
  })
}

const rename = async (ev: MouseEvent) => {
  console.log('ev', ev)
  const res = await AudioFileControllerService.updateAudioFileUsingPost({
    id: currentAudio.value.id,
    fileName: audioName.value,
  })
  if (res.code === 200) {
    Message.success('修改成功')
    await getAudioList()
  }
}

const openRename = (item: AudioFileVO) => {
  visible.value = true
  currentAudio.value = item
  audioName.value = item.fileName
}

const handleCancel = () => {
  visible.value = false
}

defineExpose({
  getAudioList,
  handelClick,
})
</script>

<style lang="scss" scoped>
.wrapper {
  position: relative;
  display: inline-block;
}

.hover-content {
  display: none;
  position: absolute;
  top: 100%;
  left: 0;
  background: #fff;
  border: 1px solid #ccc;
  padding: 10px;
  border-radius: 6px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  z-index: 10;
}
.wrapper:hover.hover-content {
  display: block;
}
.scrollbar-hide::-webkit-scrollbar {
  display: none;
}
</style>
