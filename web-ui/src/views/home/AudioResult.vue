<template>
  <div>{{ props?.file }}</div>
</template>
<script setup lang="ts">
import { AudioFileVO } from '@/api'
import { onMounted } from 'vue'
const props = defineProps<{
  file?: AudioFileVO
}>()

onMounted(() => {
  console.log('props.file', props.file)
})

const getResult = () => {
  console.log('getResult')
}

defineExpose({
  getResult,
})
</script>

<style scoped lang="scss"></style>
