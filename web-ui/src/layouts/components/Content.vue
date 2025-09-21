<template>
  <div class="h-[calc(100vh-60px)] bg-[#FFF] overflow-y-auto background-img">
    <router-view />
  </div>
</template>
<script lang="ts" setup></script>
<style lang="scss" scoped>
.background-img {
  background-image: radial-gradient(circle, rgba(0, 0, 0, 0.07) 1px, transparent 0);
  background-size: 10px 10px;
}
</style>