<template>
  <a-result :status="null">
    <template #title>
      权限不足
    </template>
    <template #subtitle>
      <a-text>Please check your permission</a-text>
    </template>
    <template #icon>
      <IconFaceSmileFill />
    </template>
    <template #extra>
      <a-space>
        <a-button type="secondary">Again</a-button>
        <a-button type="primary">Back</a-button>
      </a-space>
    </template>
  </a-result>
</template>
<script>
import { IconFaceSmileFill } from '@arco-design/web-vue/es/icon'

export default {
  components: {
    IconFaceSmileFill,
  },
}
</script>
