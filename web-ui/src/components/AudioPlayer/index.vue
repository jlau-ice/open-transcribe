<template>
  <div class="w-full flex flex-col justify-center items-center">
    <input type="range" min="0" :style="progressStyle" :max="duration" step="0.01" v-model="currentTime" @input="onSeek" class="process" />
    <div class="flex items-center justify-between w-full mt-2">
      <audio ref="audio" :src="props.src"></audio>
      <div class="text-[12px]">
        <span class="text-[#262626]">{{ formatTime(currentTime) }}</span>
        <span class="text-[#8c8c8c]">/{{ formatTime(duration) }}</span>
      </div>
      <div class="flex items-center h-[44px] gap-3">
        <span class="cursor-pointer" @click="seek(-15)"><img src="@/assets/file/l15.svg" alt="l15" /></span>
        <span class="cursor-pointer" @click="togglePlay"><img :src="isPlaying ? stop : play" alt="play" /></span>
        <span class="cursor-pointer" @click="seek(15)"><img src="@/assets/file/r15.svg" alt="r15" /></span>
      </div>
      <div class="flex items-center gap-6">
        <div class="px-3 py-1 rounded-[6px] cursor-pointer text-[12px] text-[#262626] hover:bg-[#f1f2f3] select-none" @click="changeSpeed">{{ speed }}x</div>
        <a class="cursor-pointer" :href="src" download><img src="@/assets/file/down.svg" alt="down" /></a>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import play from '@/assets/file/play.svg'
import stop from '@/assets/file/stop.svg'

import { ref, onMounted, computed } from 'vue'

const props = defineProps({
  src: { type: String, required: true },
})

const audio = ref(null)
const isPlaying = ref(false)
const currentTime = ref(0)
const duration = ref(0)
const speeds = [0.5, 1, 1.5, 2]
const speed = ref(speeds[1])
let speedIndex = 1
const emit = defineEmits(['getAudioDuration'])

onMounted(() => {
  const el = audio.value
  el.addEventListener('loadedmetadata', () => {
    duration.value = el.duration
    emit('getAudioDuration', el.duration)
  })
  el.addEventListener('timeupdate', () => {
    currentTime.value = el.currentTime
  })
  el.addEventListener('play', () => (isPlaying.value = true))
  el.addEventListener('pause', () => (isPlaying.value = false))
})

const togglePlay = () => {
  const el = audio.value
  if (el.paused) el.play()
  else el.pause()
}

const seek = (sec: number) => {
  audio.value.currentTime = Math.min(Math.max(0, audio.value.currentTime + sec), duration.value)
}

const onSeek = () => {
  audio.value.currentTime = currentTime.value
}

const changeSpeed = () => {
  speedIndex = (speedIndex + 1) % speeds.length
  speed.value = speeds[speedIndex]
  audio.value.playbackRate = speed.value
}
const formatTime = (sec: number) => {
  const m = Math.floor(sec / 60)
  const s = Math.floor(sec % 60)
  return `${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`
}

const progressStyle = computed(() => {
  const percent = duration.value ? (currentTime.value / duration.value) * 100 : 0
  return {
    background: `linear-gradient(to right, #8c8c8c ${percent}%, #eaebec ${percent}%)`,
  }
})
</script>

<style scoped>
.process {
  appearance: none;
  background-color: #eaebec;
  height: 4px;
  width: calc(100% + 40px);
}
/* Webkit 内核 (Chrome / Safari / Edge) */
.process::-webkit-slider-thumb {
  -webkit-appearance: none;
  appearance: none;
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: #262626;
  cursor: pointer;
  border: none;
}
/* Firefox */
.process::-moz-range-thumb {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: #262626;
  cursor: pointer;
  border: none;
}
</style>
