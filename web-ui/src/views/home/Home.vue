<template>
  <div class="flex">
    <side-bar @select-audio="handelSelectAudio" ref="sideBarRef" />
    <upload-content @upload-success="uploadSuccess" ref="uploadContentRef" />
  </div>
</template>

<script setup lang="ts">
import {ref} from 'vue'
import SideBar from './SideBar.vue'
import UploadContent from './UploadContent.vue'
import { AudioFileVO } from '@/api'
const sideBarRef = ref(null)
const uploadContentRef = ref(null)
const uploadSuccess = (file: AudioFileVO) => {
  sideBarRef.value.getAudioList()
  sideBarRef.value.handelClick(file)
}
const handelSelectAudio = (item: AudioFileVO) => {
  uploadContentRef.value.setCurrentAudio(item)
}
</script>
<style scoped></style>
