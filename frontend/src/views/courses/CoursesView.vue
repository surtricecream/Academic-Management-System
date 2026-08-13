<template>
  <v-row align="center">
    <v-col>
      <h2 class="text-left ml-1">Listagem de Cursos</h2>
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
  ></v-data-table>
</template>

<script setup lang="ts">
import type CourseDto from '@/models/CourseDto'
import RemoteService from '@/services/RemoteService'
import { reactive, ref } from 'vue'

let search = ref('')
let loading = ref(true)
const headers = [
  { title: 'ID', key: 'id', value: 'id', sortable: true, filterable: false },
  { title: 'Código', key: 'code', value: 'code', sortable: true, filterable: true },
  { title: 'Nome', key: 'name', value: 'name', sortable: true, filterable: true },
  { title: 'Duração (anos)', key: 'durationYears', value: 'durationYears', sortable: true, filterable: false }
]

const courses: CourseDto[] = reactive([])

getCourses()
async function getCourses() {
  courses.splice(0, courses.length)
  courses.push(...(await RemoteService.getCourses()))
  loading.value = false
}

const fuzzySearch = (value: string, search: string) => {
  let searchRegex = new RegExp(search.split('').join('.*'), 'i')
  return searchRegex.test(value)
}
</script>