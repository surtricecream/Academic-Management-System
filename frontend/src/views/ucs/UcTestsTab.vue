<template>
  <v-row align="center">
    <v-col>
      <h3 class="text-left ml-1">Testes</h3>
    </v-col>
    <v-col cols="auto">
      <div class="pa-4 text-center">
        <v-dialog v-model="createDialog" max-width="400">
          <template v-slot:activator="{ props: activatorProps }">
            <v-btn
              class="text-none font-weight-regular"
              prepend-icon="mdi-plus"
              text="Adicionar Teste"
              v-bind="activatorProps"
              color="primary"
            ></v-btn>
          </template>

          <v-card prepend-icon="mdi-file-document-edit" title="Novo Teste">
            <v-card-text>
              <v-text-field label="Título*" required v-model="newTest.title"></v-text-field>
              <v-text-field label="Data*" required type="date" :min="today" v-model="newTest.date"></v-text-field>
              <v-text-field
                label="Peso* (0.05 a 1)"
                placeholder="Ex: 0.3"
                required
                type="number"
                step="0.01"
                min="0.05"
                max="1"
                v-model.number="newTest.weight"
              ></v-text-field>
            </v-card-text>
            <v-divider></v-divider>
            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn text="Close" variant="plain" @click="createDialog = false"></v-btn>
              <v-btn
                color="primary"
                text="Save"
                variant="tonal"
                @click="
                  createDialog = false,
                  saveTest()
                "
              ></v-btn>
            </v-card-actions>
          </v-card>
        </v-dialog>
      </div>
    </v-col>
  </v-row>

  <v-data-table
    :headers="headers"
    :items="tests"
    :loading="loading"
    item-key="id"
    class="text-left"
    no-data-text="Sem testes a apresentar."
  >
    <template v-slot:[`item.actions`]="{ item }">
      <v-icon @click="openGrading(item)" class="mr-2">mdi-clipboard-check</v-icon>
      <v-icon @click="editTest(item)" class="mr-2">mdi-pencil</v-icon>
      <v-icon @click="promptDelete(item)">mdi-delete</v-icon>
    </template>
  </v-data-table>

  <v-dialog v-model="editDialog" max-width="400">
    <v-card prepend-icon="mdi-pencil" title="Editar Teste">
      <v-card-text>
        <v-text-field label="Título*" required v-model="editingTest.title"></v-text-field>
        <v-text-field label="Data*" required type="date" :min="today" v-model="editingTest.date"></v-text-field>
        <v-text-field
          label="Peso* (0.05 a 1)"
          required
          type="number"
          step="0.01"
          min="0.05"
          max="1"
          v-model.number="editingTest.weight"
        ></v-text-field>
      </v-card-text>
      <v-divider></v-divider>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn text="Close" variant="plain" @click="editDialog = false"></v-btn>
        <v-btn color="primary" text="Save" variant="tonal" @click="saveEdit"></v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>

  <ConfirmDialog
    v-model="deleteDialogOpen"
    title="Eliminar Teste"
    :message="`Tem a certeza que quer eliminar '${testToDelete?.title}'?`"
    @confirm="confirmDelete"
  />

  <TestGradeDialog
    v-model="gradeDialogOpen"
    :testId="selectedTest?.id ?? 0"
    :testTitle="selectedTest?.title ?? ''"
    :ucId="props.ucId"
  />
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import type TestDto from '@/models/evaluation/TestDto'
import type CreateTestDto from '@/models/evaluation/CreateTestDto'
import RemoteService from '@/services/RemoteService'
import ConfirmDialog from '@/components/ConfirmDialog.vue'
import TestGradeDialog from './TestGradeDialog.vue'

const props = defineProps<{ ucId: number }>()

const loading = ref(true)
const tests = ref<TestDto[]>([])
const today = new Date().toISOString().split('T')[0]

const headers = [
  { title: 'ID', key: 'id', value: 'id', sortable: true },
  { title: 'Título', key: 'title', value: 'title', sortable: true },
  { title: 'Data', key: 'date', value: 'date', sortable: true },
  { title: 'Peso', key: 'weight', value: 'weight', sortable: true },
  { title: 'Ações', key: 'actions', value: 'actions', sortable: false }
]

onMounted(getTests)

async function getTests() {
  tests.value = await RemoteService.getTests(props.ucId)
  loading.value = false
}

const createDialog = ref(false)
const newTest = ref<CreateTestDto>({ title: '', date: '', weight: undefined, ucId: props.ucId })

const saveTest = async () => {
  await RemoteService.createTest(props.ucId, newTest.value)
  newTest.value = { title: '', date: '', weight: undefined, ucId: props.ucId }
  await getTests()
}

const editDialog = ref(false)
const editingTest = ref<CreateTestDto & { id?: number }>({})

const editTest = (test: TestDto) => {
  editingTest.value = {
    id: test.id,
    title: test.title,
    date: test.date,
    weight: test.weight,
    ucId: props.ucId
  }
  editDialog.value = true
}

const saveEdit = async () => {
  await RemoteService.updateTest(props.ucId, editingTest.value.id!, editingTest.value)
  editDialog.value = false
  await getTests()
}

const deleteDialogOpen = ref(false)
const testToDelete = ref<TestDto | null>(null)

const promptDelete = (test: TestDto) => {
  testToDelete.value = test
  deleteDialogOpen.value = true
}

const confirmDelete = async () => {
  await RemoteService.deleteTest(props.ucId, testToDelete.value!.id!)
  await getTests()
}

const gradeDialogOpen = ref(false)
const selectedTest = ref<TestDto | null>(null)

const openGrading = (test: TestDto) => {
  selectedTest.value = test
  gradeDialogOpen.value = true
}
</script>