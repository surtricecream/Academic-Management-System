<template>
  <v-dialog v-model="show">
    <v-alert v-model="show" type="success" closable close-text="Fechar alerta">
      {{ appearanceStore.successMessage ?? '' }}
    </v-alert>
  </v-dialog>
</template>

<script setup lang="ts">
import { useAppearanceStore } from '@/stores/appearance'
import { ref, watchEffect } from 'vue'

const appearanceStore = useAppearanceStore()

const show = ref(false)
watchEffect(() => (show.value = appearanceStore.successMessage !== null))
</script>

<style scoped lang="scss">
.v-dialog__container {
  display: unset !important;
}

.v-alert {
  z-index: 9999;
  position: absolute;
  left: 20px;
  top: 140px;
  width: calc(100% - 40px);
}
</style>