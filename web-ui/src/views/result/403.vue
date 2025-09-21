<template>
  <a-result status="403">
    <template #subtitle>
      <a-text>无权限访问</a-text>
    </template>
    <template #extra>
      <a-space>
        <a-button @click="goBack" type="primary">Back</a-button>
      </a-space>
    </template>
  </a-result>
</template>
<script lang="ts" setup>
const goBack = () => {
  window.history.back();
};
</script>
