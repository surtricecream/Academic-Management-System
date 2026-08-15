<template>
  <div class="pa-4 text-center">
    <v-dialog v-model="dialog" max-width="400">
      <template v-slot:activator="{ props: activatorProps }">
        <v-btn
          class="text-none font-weight-regular"
          prepend-icon="mdi-plus"
          text="Adicionar Curso"
          v-bind="activatorProps"
          color="primary"
        ></v-btn>
      </template>

      <v-card prepend-icon="mdi-school" title="Novo Curso">
        <v-card-text>
          <v-text-field label="Código*" placeholder="Ex: LEIC" required v-model="newCourse.code"></v-text-field>
          <v-text-field label="Nome*" placeholder="Ex: Licenciatura em Engenharia Informática e de Computadores" required v-model="newCourse.name"></v-text-field>
          <v-text-field
            label="Duração (anos)*"
            required
            type="number"
            v-model.number="newCourse.durationYears"
          ></v-text-field>
        </v-card-text>

        <v-divider></v-divider>

        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn text="Close" variant="plain" @click="dialog = false"></v-btn>
          <v-btn
            color="primary"
            text="Save"
            variant="tonal"
            @click="
              dialog = false,
              saveCourse()
            "
          ></v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import type CourseDto from '../../models/courses/CourseDto'
import RemoteService from '@/services/RemoteService'

const dialog = ref(false)
const emit = defineEmits(['course-created'])

const newCourse = ref<CourseDto>({
  code: '',
  name: '',
  durationYears: undefined
})

const saveCourse = async () => {
  await RemoteService.createCourse(newCourse.value)
  newCourse.value = { code: '', name: '', durationYears: undefined }
  emit('course-created')
}
</script>