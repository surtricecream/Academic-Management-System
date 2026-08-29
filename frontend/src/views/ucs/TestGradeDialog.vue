<template>
  <v-dialog v-model="dialogOpen" max-width="500">
    <v-card prepend-icon="mdi-file-document-edit" :title="`Notas - ${testTitle}`">
      <v-card-text>
        <v-table>
          <thead>
            <tr>
              <th class="text-left">Aluno</th>
              <th class="text-left">Nota</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="student in students" :key="student.id">
              <td>{{ student.name }}</td>
              <td>
                <v-text-field
                  type="number"
                  step="0.1"
                  min="0"
                  max="20"
                  density="compact"
                  hide-details
                  v-model.number="scores[student.id!]"
                ></v-text-field>
              </td>
              <td>
                <v-btn size="small" color="primary" @click="saveGrade(student.id!)">Guardar</v-btn>
              </td>
            </tr>
          </tbody>
        </v-table>
      </v-card-text>

      <v-divider></v-divider>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn text="Close" variant="plain" @click="dialogOpen = false"></v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import type PersonDto from '@/models/people/PersonDto'
import RemoteService from '@/services/RemoteService'
import { useAppearanceStore } from '@/stores/appearance'

const props = defineProps<{
  modelValue: boolean
  testId: number
  testTitle: string
  ucId: number
}>()
const emit = defineEmits(['update:modelValue'])

const dialogOpen = ref(props.modelValue)
watch(() => props.modelValue, (v) => {
  dialogOpen.value = v
  if (v) loadData()
})
watch(dialogOpen, (v) => emit('update:modelValue', v))

const students = ref<PersonDto[]>([])
const scores = reactive<Record<number, number>>({})

async function loadData() {
  Object.keys(scores).forEach((key) => delete scores[Number(key)])

  const members = await RemoteService.getUcMembers(props.ucId)
  const aluno = members.filter((m) => m.role === 'ALUNO')
  students.value = aluno.map((m) => ({ id: m.personId, name: m.personName }))

  for (const student of students.value) {
    const gradesData = await RemoteService.getStudentGrades(props.ucId, student.id!)
    const existing = gradesData.grades?.find((g: any) => g.testId === props.testId)
    if (existing) {
      scores[student.id!] = existing.score
    }
  }
}

const appearanceStore = useAppearanceStore()

const saveGrade = async (personId: number) => {
  await RemoteService.gradeTest(props.testId, {
    testId: props.testId,
    projectId: null,
    personId,
    groupId: null,
    score: scores[personId]
  })
  console.log('about to push success')
  appearanceStore.pushSuccess('Nota atribuída com sucesso')
  console.log('pushed, current value:', appearanceStore.successMessage)
}
</script>