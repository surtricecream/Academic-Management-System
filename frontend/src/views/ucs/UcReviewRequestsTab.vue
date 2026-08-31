<template>
  <v-row align="center">
    <v-col>
      <h3 class="text-left ml-1">Pedidos de Revisão</h3>
    </v-col>
  </v-row>

  <v-data-table
    :headers="headers"
    :items="requests"
    :loading="loading"
    item-key="id"
    class="text-left"
    no-data-text="Sem pedidos de revisão a apresentar."
  >
    <template v-slot:[`item.status`]="{ item }">
      <v-chip v-if="item.status === 'PENDING'" color="orange" text-color="white">Pendente</v-chip>
      <v-chip v-else-if="item.status === 'ASSISTANT_REVIEWED'" color="blue" text-color="white">Revisto por Assistente</v-chip>
      <v-chip v-else-if="item.status === 'APPROVED'" color="green" text-color="white">Aprovado</v-chip>
      <v-chip v-else color="red" text-color="white">Rejeitado</v-chip>
    </template>
    <template v-slot:[`item.actions`]="{ item }">
      <v-btn size="small" @click="openDetail(item)">Ver Detalhes</v-btn>
    </template>
  </v-data-table>

  <v-dialog v-model="detailDialog" max-width="500">
    <v-card v-if="selected" :title="`Pedido - ${selected.testTitle}`">
      <v-card-text>
        <p><strong>Aluno:</strong> {{ selected.studentName }}</p>
        <p><strong>Justificação:</strong> {{ selected.justification }}</p>
        <p><strong>Prazo:</strong> {{ selected.deadline }}</p>

        <div v-if="selected.assistantOpinion">
          <p class="mt-2"><strong>Parecer do Assistente ({{ selected.assistantName }}):</strong></p>
          <p>{{ selected.assistantOpinion }}</p>
        </div>

        <div v-if="selected.regenteDecision">
          <p class="mt-2"><strong>Decisão ({{ selected.decidedByName }}):</strong> {{ selected.status === 'APPROVED' ? 'Aprovado' : 'Rejeitado' }}</p>
          <p>{{ selected.regenteDecision }}</p>
        </div>

        <div v-if="selected.status === 'PENDING' && !props.isRegente" class="mt-4">
          <v-textarea label="Parecer" v-model="opinionText"></v-textarea>
          <v-btn size="small" color="primary" @click="submitOpinion">Submeter Parecer</v-btn>
        </div>

        <div v-if="selected.status === 'PENDING' || selected.status === 'ASSISTANT_REVIEWED'" class="mt-4">
          <v-textarea label="Nota de Decisão" v-model="decisionNote"></v-textarea>
          <v-btn size="small" color="success" class="mr-2" @click="decide(true)">Aprovar</v-btn>
          <v-btn size="small" color="error" @click="decide(false)">Rejeitar</v-btn>
        </div>
      </v-card-text>
      <v-divider></v-divider>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn text="Close" variant="plain" @click="detailDialog = false"></v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import type ReviewRequestDto from '@/models/evaluation/ReviewRequestDto'
import RemoteService from '@/services/RemoteService'
import { useAppearanceStore } from '@/stores/appearance'

const props = defineProps<{ ucId: number; isRegente: boolean }>()
const appearanceStore = useAppearanceStore()

const loading = ref(true)
const requests = ref<ReviewRequestDto[]>([])

const headers = [
  { title: 'ID', key: 'id', value: 'id', sortable: true },
  { title: 'Teste', key: 'testTitle', value: 'testTitle', sortable: true },
  { title: 'Aluno', key: 'studentName', value: 'studentName', sortable: true },
  { title: 'Estado', key: 'status', value: 'status', sortable: true },
  { title: 'Ações', key: 'actions', value: 'actions', sortable: false }
]

onMounted(getRequests)

async function getRequests() {
  requests.value = await RemoteService.getUcReviewRequests(props.ucId)
  loading.value = false
}

const detailDialog = ref(false)
const selected = ref<ReviewRequestDto | null>(null)
const opinionText = ref('')
const decisionNote = ref('')

const openDetail = (request: ReviewRequestDto) => {
  selected.value = request
  opinionText.value = ''
  decisionNote.value = ''
  detailDialog.value = true
}

const submitOpinion = async () => {
  await RemoteService.addAssistantOpinion(selected.value!.id!, opinionText.value)
  detailDialog.value = false
  appearanceStore.pushSuccess('Parecer submetido com sucesso')
  await getRequests()
}

const decide = async (approved: boolean) => {
  await RemoteService.decideReviewRequest(selected.value!.id!, approved, decisionNote.value)
  detailDialog.value = false
  appearanceStore.pushSuccess('Decisão registada com sucesso')
  await getRequests()
}
</script>