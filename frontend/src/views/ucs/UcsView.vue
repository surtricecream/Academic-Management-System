<template>
  <v-row align="center">
    <v-col>
      <h2 class="text-left ml-1">Listagem de Unidades Curriculares</h2>
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
    :items="ucs"
    :search="search"
    :loading="loading"
    :custom-filter="fuzzySearch"
    item-key="id"
    class="text-left"
    no-data-text="Sem UCs a apresentar."
  >
    <template v-slot:[`item.courseNames`]="{ item }">
      {{ item.courseNames?.join(', ') }}
    </template>
    <template v-slot:[`item.actions`]="{ item }">
      <v-icon @click="goToUc(item)">mdi-arrow-right</v-icon>
    </template>
  </v-data-table>
</template>

<script setup lang="ts">
import type UcDto from '../../models/ucs/UcDto'
import RemoteService from '@/services/RemoteService'
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

let search = ref('')
let loading = ref(true)
const router = useRouter()

const headers = [
  { title: 'ID', key: 'id', value: 'id', sortable: true, filterable: false },
  { title: 'Código', key: 'code', value: 'code', sortable: true, filterable: true },
  { title: 'Nome', key: 'name', value: 'name', sortable: true, filterable: true },
  { title: 'Semestre', key: 'semester', value: 'semester', sortable: true, filterable: false },
  { title: 'Curso(s)', key: 'courseNames', value: 'courseNames', sortable: false, filterable: false },
  { title: 'Regente', key: 'regenteName', value: 'regenteName', sortable: true, filterable: true },
  { title: 'Ações', key: 'actions', value: 'actions', sortable: false, filterable: false }
]

const ucs: UcDto[] = reactive([])

getUcs()
async function getUcs() {
  ucs.splice(0, ucs.length)
  ucs.push(...(await RemoteService.getUcs()))
  loading.value = false
}

const goToUc = (uc: UcDto) => {
  router.push(`/ucs/${uc.id}`)
}

const fuzzySearch = (value: string, search: string) => {
  let searchRegex = new RegExp(search.split('').join('.*'), 'i')
  return searchRegex.test(value)
}
</script>