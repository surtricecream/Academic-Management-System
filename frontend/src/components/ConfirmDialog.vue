<template>
  <v-dialog v-model="internalOpen" max-width="400">
    <v-card :title="title">
      <v-card-text>{{ message }}</v-card-text>
      <v-divider></v-divider>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn text="Cancelar" variant="plain" @click="cancel"></v-btn>
        <v-btn color="error" text="Eliminar" variant="tonal" @click="confirmAction"></v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'

const props = defineProps<{
  modelValue: boolean
  title?: string
  message?: string
}>()

const emit = defineEmits(['update:modelValue', 'confirm'])

const internalOpen = ref(props.modelValue)
watch(() => props.modelValue, (v) => (internalOpen.value = v))
watch(internalOpen, (v) => emit('update:modelValue', v))

const cancel = () => {
  internalOpen.value = false
}
const confirmAction = () => {
  internalOpen.value = false
  emit('confirm')
}
</script>