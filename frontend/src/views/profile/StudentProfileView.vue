<template>
  <div v-if="profile">
    <h2 class="text-left ml-1">{{ profile.personName }}</h2>

    <h3 class="text-left ml-1 mt-4">As Minhas UCs</h3>
    <v-table>
      <thead>
        <tr>
          <th class="text-left">UC</th>
          <th class="text-left">Média</th>
          <th class="text-left">Ações</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="uc in profile.ucs" :key="uc.ucId">
          <td>{{ uc.ucName }}</td>
          <td>{{ uc.average == null ? 'Sem notas' : uc.average.toFixed(2) }}</td>
          <td>
            <v-btn size="small" variant="tonal" color="primary" @click="selectUc(uc.ucId)">
              Ver Notas
            </v-btn>
          </td>
        </tr>
      </tbody>
    </v-table>

    <h3 class="text-left ml-1 mt-6">Avaliações Pendentes</h3>
    <v-table>
      <thead>
        <tr>
          <th class="text-left">Tipo</th>
          <th class="text-left">Título</th>
          <th class="text-left">UC</th>
          <th class="text-left">Prazo</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="ev in profile.pendingEvaluations" :key="`${ev.type}-${ev.id}`">
          <td>{{ ev.type === 'TEST' ? 'Teste' : 'Projeto' }}</td>
          <td>{{ ev.title }}</td>
          <td>{{ ev.ucName }}</td>
          <td>{{ ev.deadline }}</td>
        </tr>
      </tbody>
    </v-table>

    <v-dialog v-model="ucGradesDialog" max-width="500">
      <v-card v-if="selectedUcGrades" :title="`Notas - ${selectedUcGrades.ucName}`">
        <v-card-text>
          <v-table>
            <thead>
              <tr>
                <th class="text-left">Avaliação</th>
                <th class="text-left">Nota</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="grade in selectedUcGrades.grades" :key="grade.id">
                <td>{{ grade.testTitle ?? grade.projectTitle }}</td>
                <td>{{ grade.score }}</td>
              </tr>
            </tbody>
          </v-table>
          <p class="mt-2">
            <strong>Média: {{ selectedUcGrades.average == null ? 'Sem notas' : selectedUcGrades.average.toFixed(2) }}</strong>
          </p>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn text="Close" variant="plain" @click="ucGradesDialog = false"></v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import type StudentProfileDto from '@/models/evaluation/StudentProfileDto'
import type StudentGradesDto from '@/models/evaluation/StudentGradesDto'
import RemoteService from '@/services/RemoteService'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const profile = ref<StudentProfileDto | null>(null)

onMounted(async () => {
  profile.value = await RemoteService.getMyProfile(authStore.personId!)
})

const ucGradesDialog = ref(false)
const selectedUcGrades = ref<StudentGradesDto | null>(null)

const selectUc = async (ucId: number) => {
  selectedUcGrades.value = await RemoteService.getStudentGrades(ucId, authStore.personId!)
  ucGradesDialog.value = true
}
</script>