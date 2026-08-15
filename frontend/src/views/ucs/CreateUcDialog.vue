<template>
  <div class="pa-4 text-center">
    <v-dialog v-model="dialog" max-width="500">
      <template v-slot:activator="{ props: activatorProps }">
        <v-btn
          class="text-none font-weight-regular"
          prepend-icon="mdi-plus"
          text="Adicionar UC"
          v-bind="activatorProps"
          color="primary"
        ></v-btn>
      </template>

      <v-card prepend-icon="mdi-book-open-variant" title="Nova UC">
        <v-card-text>
          <v-text-field label="Código*" placeholder="Ex: PO" required v-model="newUc.code"></v-text-field>
          <v-text-field label="Nome*" placeholder="Ex: Programação com Objectos" required v-model="newUc.name"></v-text-field>
          <v-select :items="[1, 2]" label="Semestre*" required v-model="newUc.semester"></v-select>
          <v-select :items="[3, 6]" label="ECTS*" required v-model="newUc.ects"></v-select>
          <v-select
            :items="teachers"
            item-title="name"
            item-value="id"
            label="Professor Regente*"
            required
            v-model="newUc.regenteId"
          ></v-select>
          <v-select
            :items="courses"
            item-title="name"
            item-value="id"
            label="Curso(s)*"
            multiple
            required
            v-model="newUc.courseIds"
          ></v-select>
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
              saveUc()
            "
          ></v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import type CreateUcDto from '../../models/ucs/CreateUcDto'
import type PersonDto from '../../models/people/PersonDto'
import type CourseDto from '../../models/courses/CourseDto'
import RemoteService from '@/services/RemoteService'

const dialog = ref(false)
const emit = defineEmits(['uc-created'])

const teachers = ref<PersonDto[]>([])
const courses = ref<CourseDto[]>([])

onMounted(async () => {
  const people = await RemoteService.getPeople()
  teachers.value = people.filter((p) => p.type === 'MAIN_TEACHER')
  courses.value = await RemoteService.getCourses()
})

const newUc = ref<CreateUcDto>({
  code: '',
  name: '',
  semester: undefined,
  ects: undefined,
  regenteId: undefined,
  courseIds: []
})

const saveUc = async () => {
  await RemoteService.createUc(newUc.value)
  newUc.value = { code: '', name: '', semester: undefined, ects: undefined, regenteId: undefined, courseIds: [] }
  emit('uc-created')
}
</script>