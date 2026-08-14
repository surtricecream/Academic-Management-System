<template>
  <v-row align="center">
    <v-col>
      <h2 class="text-left ml-1">Listagem de Cursos</h2>
    </v-col>
    <v-col cols="auto">
      <CreateCourseDialog @course-created="getCourses" />
    </v-col>
  </v-row>

  <v-text-field
    v-model="search"
    label="Search"
    prepend-inner-icon="mdi-magnify"
    variant="outlined"
    hide-details
    single-line
  ></v-text-field>

  <v-data-table
    :headers="headers"
    :items="courses"
    :search="search"
    :loading="loading"
    :custom-filter="fuzzySearch"
    item-key="id"
    class="text-left"
    no-data-text="Sem cursos a apresentar."
  >
    <template v-slot:[`item.actions`]="{ item }">
      <v-icon @click="editCourse(item)" class="mr-2">mdi-pencil</v-icon>
      <v-icon @click="promptDelete(item)">mdi-delete</v-icon>
    </template>
  </v-data-table>

  <ConfirmDialog
    v-model="deleteDialogOpen"
    title="Eliminar Curso"
    :message="`Tem a certeza que quer eliminar '${courseToDelete?.name}'?`"
    @confirm="confirmDelete"
  />

  <v-dialog v-model="editDialog" max-width="400">
    <v-card prepend-icon="mdi-school" title="Editar Curso">
      <v-card-text>
        <v-text-field label="Código*" required v-model="editingCourse.code"></v-text-field>
        <v-text-field label="Nome*" required v-model="editingCourse.name"></v-text-field>
        <v-text-field label="Duração (anos)*" required type="number" v-model.number="editingCourse.durationYears"></v-text-field>
      </v-card-text>
      <v-divider></v-divider>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn text="Close" variant="plain" @click="editDialog = false"></v-btn>
        <v-btn color="primary" text="Save" variant="tonal" @click="saveEdit"></v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import type CourseDto from '../../models/courses/CourseDto'
import RemoteService from '@/services/RemoteService'
import CreateCourseDialog from './CreateCourseDialog.vue'
import ConfirmDialog from '@/components/ConfirmDialog.vue'
import { reactive, ref } from 'vue'

let search = ref('')
let loading = ref(true)
const headers = [
  { title: 'ID', key: 'id', value: 'id', sortable: true, filterable: false },
  { title: 'Código', key: 'code', value: 'code', sortable: true, filterable: true },
  { title: 'Nome', key: 'name', value: 'name', sortable: true, filterable: true },
  { title: 'Duração (anos)', key: 'durationYears', value: 'durationYears', sortable: true, filterable: false },
  { title: 'Ações', key: 'actions', value: 'actions', sortable: false, filterable: false }
]

const courses: CourseDto[] = reactive([])

getCourses()
async function getCourses() {
  courses.splice(0, courses.length)
  courses.push(...(await RemoteService.getCourses()))
  loading.value = false
}

const editDialog = ref(false)
const editingCourse = ref<CourseDto>({})

const editCourse = (course: CourseDto) => {
  editingCourse.value = { ...course }
  editDialog.value = true
}

const saveEdit = async () => {
  await RemoteService.updateCourse(editingCourse.value.id!, editingCourse.value)
  editDialog.value = false
  await getCourses()
}

const deleteDialogOpen = ref(false)
const courseToDelete = ref<CourseDto | null>(null)

const promptDelete = (course: CourseDto) => {
  courseToDelete.value = course
  deleteDialogOpen.value = true
}

const confirmDelete = async () => {
  await RemoteService.deleteCourse(courseToDelete.value!.id!)
  await getCourses()
}

const fuzzySearch = (value: string, search: string) => {
  let searchRegex = new RegExp(search.split('').join('.*'), 'i')
  return searchRegex.test(value)
}
</script>