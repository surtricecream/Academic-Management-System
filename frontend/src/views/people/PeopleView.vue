<template>
  <v-row align="center">
    <v-col>
      <h2 class="text-left ml-1">Listagem de Pessoas</h2>
    </v-col>
    <v-col cols="auto">
      <CreatePersonDialog @person-created="getPeople" />
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
    :items="people"
    :search="search"
    :loading="loading"
    :custom-filter="fuzzySearch"
    item-key="id"
    class="text-left"
    no-data-text="Sem pessoas a apresentar."
  >
    <template v-slot:[`item.type`]="{ item }">
      <v-chip v-if="item.type === 'ADMINISTRATOR'" color="purple" text-color="white">
        Administrador
      </v-chip>
      <v-chip v-else-if="item.type === 'MAIN_TEACHER'" color="red" text-color="white">
        Professor Regente
      </v-chip>
      <v-chip v-else-if="item.type === 'TEACHING_ASSISTANT'" color="blue" text-color="white">
        Professor Assistente
      </v-chip>
      <v-chip v-else color="green" text-color="white">
        Aluno
      </v-chip>
    </template>
    <template v-slot:[`item.actions`]="{ item }">
      <v-icon @click="editPerson(item)" class="mr-2">mdi-pencil</v-icon>
      <v-icon @click="promptDelete(item)">mdi-delete</v-icon>
    </template>
  </v-data-table>

  <ConfirmDialog
    v-model="deleteDialogOpen"
    title="Eliminar Pessoa"
    :message="`Tem a certeza que quer eliminar '${personToDelete?.name}'?`"
    @confirm="confirmDelete"
  />

  <v-dialog v-model="editDialog" max-width="400">
    <v-card prepend-icon="mdi-account" title="Editar Pessoa">
      <v-card-text>
        <v-text-field label="Nome*" required v-model="editingPerson.name"></v-text-field>
        <v-text-field label="IST ID*" required v-model="editingPerson.istId"></v-text-field>
        <v-text-field label="Email*" required v-model="editingPerson.email"></v-text-field>
        <v-select
          :items="['Administrador', 'Professor Regente', 'Professor Assistente', 'Aluno']"
          label="Categoria*"
          required
          v-model="editSelectedType"
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
import type PersonDto from '@/models/people/PersonDto.js'
import RemoteService from '@/services/RemoteService'
import CreatePersonDialog from './CreatePersonDialog.vue'
import ConfirmDialog from '@/components/ConfirmDialog.vue'
import { reactive, ref } from 'vue'

let search = ref('')
let loading = ref(true)
const headers = [
  { 
    title: 'ID', 
    key: 'id', 
    value: 'id', 
    sortable: true, 
    filterable: false
  },
  {
    title: 'Nome',
    key: 'name',
    value: 'name',
    sortable: true,
    filterable: true
  },
  {
    title: 'IST ID',
    key: 'istId',
    value: 'istId',
    sortable: true,
    filterable: true
  },
  {
    title: 'Tipo',
    key: 'type',
    value: 'type',
    sortable: true,
    filterable: true
  },
  {
    title: 'Ações',
    key: 'actions',
    value: 'actions',
    sortable: false,
    filterable: false
  }
]

const people: PersonDto[] = reactive([])

getPeople()
async function getPeople() {
  people.splice(0, people.length)
  people.push(...(await RemoteService.getPeople()))
  loading.value = false
  console.log(people)
}

const typeMappings: Record<string, string> = {
  Administrador: 'ADMINISTRATOR',
  'Professor Regente': 'MAIN_TEACHER',
  'Professor Assistente': 'TEACHING_ASSISTANT',
  Aluno: 'STUDENT'
}
const reverseTypeMappings: Record<string, string> = Object.fromEntries(
  Object.entries(typeMappings).map(([k, v]) => [v, k])
)

const editDialog = ref(false)
const editingPerson = ref<PersonDto>({})
const editSelectedType = ref('')

const editPerson = (person: PersonDto) => {
  editingPerson.value = { ...person }
  editSelectedType.value = reverseTypeMappings[person.type ?? ''] ?? ''
  editDialog.value = true
}

const saveEdit = async () => {
  editingPerson.value.type = typeMappings[editSelectedType.value]
  await RemoteService.updatePerson(editingPerson.value.id!, editingPerson.value)
  editDialog.value = false
  await getPeople()
}

const deleteDialogOpen = ref(false)
const personToDelete = ref<PersonDto | null>(null)

const promptDelete = (person: PersonDto) => {
  personToDelete.value = person
  deleteDialogOpen.value = true
}

const confirmDelete = async () => {
  await RemoteService.deletePerson(personToDelete.value!.id!)
  await getPeople()
}

const fuzzySearch = (value: string, search: string) => {
  let searchRegex = new RegExp(search.split('').join('.*'), 'i')
  return searchRegex.test(value)
}

</script>
