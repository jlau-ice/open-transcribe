<template>
  <div class="overflow-y-auto h-[calc(100vh-100px)]">
    <template v-for="item in resultList" :key="item?.id">
      <div class="text-[#262626]">{{ item?.resultText }}</div>
    </template>
  </div>
</template>
<script setup lang="ts">
import { AudioFileVO } from '@/api'
import { onMounted, ref, watch } from 'vue'
import { TranscribeResultControllerService } from '@/api'
import { type TranscribeResultVO } from '@/api'
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

const getResult = async (id: number) => {
  const res = await TranscribeResultControllerService.listUsingGet(id)
  resultList.value = res.data
}
</script>

<style scoped lang="scss"></style>
