<template>
  <v-dialog v-model="dialogOpen" max-width="500">
    <v-card prepend-icon="mdi-folder-edit" :title="`Notas - ${projectTitle}`">
      <v-card-text>
        <v-table>
          <thead>
            <tr>
              <th class="text-left">{{ isGroupProject ? 'Grupo' : 'Aluno' }}</th>
              <th class="text-left">Nota</th>
              <th></th>
            </tr>
          </thead>
          <tbody v-if="!isGroupProject">
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
                <v-btn size="small" color="primary" @click="saveIndividualGrade(student.id!)">Guardar</v-btn>
              </td>
            </tr>
          </tbody>
          <tbody v-else>
            <tr v-for="(group, index) in groups" :key="group.id">
              <td>Grupo {{ index + 1 }}: {{ group.memberNames?.join(', ') }}</td>
              <td>
                <v-text-field
                  type="number"
                  step="0.1"
                  min="0"
                  max="20"
                  density="compact"
                  hide-details
                  v-model.number="groupScores[group.id!]"
                ></v-text-field>
              </td>
              <td>
                <v-btn size="small" color="primary" @click="saveGroupGrade(group.id!)">Guardar</v-btn>
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
import type ProjectGroupDto from '@/models/evaluation/ProjectGroupDto'
import RemoteService from '@/services/RemoteService'
import { useAppearanceStore } from '@/stores/appearance'

const props = defineProps<{
  modelValue: boolean
  projectId: number
  projectTitle: string
  isGroupProject: boolean
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

const groups = ref<ProjectGroupDto[]>([])
const groupScores = reactive<Record<number, number>>({})

async function loadData() {
  Object.keys(scores).forEach((key) => delete scores[Number(key)])

  if (props.isGroupProject) {
    groups.value = await RemoteService.getGroups(props.projectId)
    for (const group of groups.value) {
      //TODO: add project group lookup to add pre-filled grades
    }
  } else {
    const members = await RemoteService.getUcMembers(props.ucId)
    const aluno = members.filter((m) => m.role === 'ALUNO')
    students.value = aluno.map((m) => ({ id: m.personId, name: m.personName }))

    for (const student of students.value) {
      const gradesData = await RemoteService.getStudentGrades(props.ucId, student.id!)
      const existing = gradesData.grades?.find((g: any) => g.projectId === props.projectId)
      if (existing) {
        scores[student.id!] = existing.score
      }
    }
  }
}

const appearanceStore = useAppearanceStore()

const saveIndividualGrade = async (personId: number) => {
  await RemoteService.gradeIndividualProject(props.projectId, {
    testId: null,
    projectId: props.projectId,
    personId,
    groupId: null,
    score: scores[personId]
  })
  appearanceStore.pushSuccess('Nota atribuída com sucesso')
}

const saveGroupGrade = async (groupId: number) => {
  await RemoteService.gradeGroupProject(props.projectId, {
    testId: null,
    projectId: props.projectId,
    personId: null,
    groupId,
    score: groupScores[groupId]
  })
  appearanceStore.pushSuccess('Nota atribuída com sucesso')
}
</script>