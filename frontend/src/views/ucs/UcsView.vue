<template>
  <v-row align="center">
    <v-col>
      <h2 class="text-left ml-1">Listagem de Unidades Curriculares</h2>
    </v-col>
    <v-col cols="auto">
      <CreateUcDialog @uc-created="getUcs" />
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
      <v-icon @click="editUc(item)" class="mr-2">mdi-pencil</v-icon>
      <v-icon @click="promptDelete(item)" class="mr-2">mdi-delete</v-icon>
      <v-icon @click="goToUc(item)">mdi-arrow-right</v-icon>
    </template>
  </v-data-table>

  <ConfirmDialog
    v-model="deleteDialogOpen"
    title="Eliminar UC"
    :message="`Tem a certeza que quer eliminar '${ucToDelete?.name}'?`"
    @confirm="confirmDelete"
  />

  <v-dialog v-model="editDialog" max-width="500">
    <v-card prepend-icon="mdi-book-open-variant" title="Editar UC">
      <v-card-text>
        <v-text-field label="Código*" required v-model="editingUc.code"></v-text-field>
        <v-text-field label="Nome*" required v-model="editingUc.name"></v-text-field>
        <v-select :items="[1, 2]" label="Semestre*" required v-model="editingUc.semester"></v-select>
        <v-select :items="[3, 6]" label="ECTS*" required v-model="editingUc.ects"></v-select>
        <v-select
          :items="teachers"
          item-title="name"
          item-value="id"
          label="Professor Regente*"
          required
          v-model="editingUc.regenteId"
        ></v-select>
        <v-select
          :items="courses"
          item-title="name"
          item-value="id"
          label="Curso(s)*"
          multiple
          required
          v-model="editingUc.courseIds"
        ></v-select>
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
import type UcDto from '../../models/ucs/UcDto'
import type CreateUcDto from '../../models/ucs/CreateUcDto'
import type PersonDto from '../../models/people/PersonDto'
import type CourseDto from '../../models/courses/CourseDto'
import RemoteService from '@/services/RemoteService'
import CreateUcDialog from './CreateUcDialog.vue'
import ConfirmDialog from '@/components/ConfirmDialog.vue'
import { reactive, ref, onMounted } from 'vue'
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
const teachers = ref<PersonDto[]>([])
const courses = ref<CourseDto[]>([])

onMounted(async () => {
  const people = await RemoteService.getPeople()
  teachers.value = people.filter((p) => p.type === 'MAIN_TEACHER')
  courses.value = await RemoteService.getCourses()
})

getUcs()
async function getUcs() {
  ucs.splice(0, ucs.length)
  ucs.push(...(await RemoteService.getUcs()))
  loading.value = false
}

const goToUc = (uc: UcDto) => {
  router.push(`/ucs/${uc.id}`)
}

const editDialog = ref(false)
const editingUc = ref<CreateUcDto & { id?: number }>({})

const editUc = (uc: UcDto) => {
  editingUc.value = {
    id: uc.id,
    code: uc.code,
    name: uc.name,
    semester: uc.semester,
    ects: uc.ects,
    regenteId: uc.regenteId,
    courseIds: uc.courseIds
  }
  editDialog.value = true
}

const saveEdit = async () => {
  await RemoteService.updateUc(editingUc.value.id!, editingUc.value)
  editDialog.value = false
  await getUcs()
}

const deleteDialogOpen = ref(false)
const ucToDelete = ref<UcDto | null>(null)

const promptDelete = (uc: UcDto) => {
  ucToDelete.value = uc
  deleteDialogOpen.value = true
}

const confirmDelete = async () => {
  await RemoteService.deleteUc(ucToDelete.value!.id!)
  await getUcs()
}

const fuzzySearch = (value: string, search: string) => {
  let searchRegex = new RegExp(search.split('').join('.*'), 'i')
  return searchRegex.test(value)
}
</script>