<template>
  <div class="flex">
    <side-bar ref="sideBarRef" />
    <upload-content @upload-success="uploadSuccess"/>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import SideBar from './SideBar.vue'
import UploadContent from './UploadContent.vue'
const sideBarRef = ref(null)
const uploadSuccess = () => {
  sideBarRef.value.getAudioList()
}
</script>
<style scoped></style>
