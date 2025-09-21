<template>
  <a-layout>
    <a-layout-header>
      <Header />
    </a-layout-header>
    <a-layout-content>
      <Content />
    </a-layout-content>
    <!-- <a-layout-footer>
      <Footer />
    </a-layout-footer> -->
  </a-layout>
</template>
<script lang="ts" setup>
import Header from '@/layouts/components/Header.vue'
import Content from '@/layouts/components/Content.vue'
// import Footer from '@/layouts/components/Footer.vue';
</script>
<style lang="scss" scoped></style>
