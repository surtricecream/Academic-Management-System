<template>
  <v-row align="center">
    <v-col>
      <h3 class="text-left ml-1">Membros da UC</h3>
    </v-col>
    <v-col cols="auto">
      <div class="pa-4 text-center">
        <v-dialog v-model="addDialog" max-width="400">
          <template v-slot:activator="{ props: activatorProps }">
            <v-btn
              class="text-none font-weight-regular"
              prepend-icon="mdi-plus"
              text="Adicionar Membro"
              v-bind="activatorProps"
              color="primary"
            ></v-btn>
          </template>

          <v-card prepend-icon="mdi-account-plus" title="Adicionar Membro">
            <v-card-text>
              <v-card-text>
                <v-select
                  :items="['Aluno', 'Professor Assistente']"
                  label="Categoria*"
                  required
                  v-model="selectedRole"
                ></v-select>
                <v-select
                  :items="filteredPeople"
                  item-title="name"
                  item-value="id"
                  label="Pessoas*"
                  multiple
                  required
                  v-model="selectedPersonIds"
                ></v-select>
            </v-card-text>
            </v-card-text>
            <v-divider></v-divider>
            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn text="Close" variant="plain" @click="addDialog = false"></v-btn>
              <v-btn
                color="primary"
                text="Save"
                variant="tonal"
                @click="
                  addDialog = false,
                  saveMember()
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
    :items="members"
    :loading="loading"
    item-key="id"
    class="text-left"
    no-data-text="Sem membros a apresentar."
  >
    <template v-slot:[`item.role`]="{ item }">
      <v-chip v-if="item.role === 'ASSISTENTE'" color="blue" text-color="white">Professor Assistente</v-chip>
      <v-chip v-else color="green" text-color="white">Aluno</v-chip>
    </template>
    <template v-slot:[`item.actions`]="{ item }">
      <v-icon @click="promptRemove(item)">mdi-delete</v-icon>
    </template>
  </v-data-table>

  <ConfirmDialog
    v-model="removeDialogOpen"
    title="Remover Membro"
    :message="`Tem a certeza que quer remover '${memberToRemove?.personName}' desta UC?`"
    @confirm="confirmRemove"
  />
</template>

<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue'
import type UcMembershipDto from '@/models/ucmemberships/UcMembershipDto'
import type AddMembershipDto from '@/models/ucmemberships/AddMembershipDto'
import type PersonDto from '@/models/people/PersonDto'
import RemoteService from '@/services/RemoteService'
import ConfirmDialog from '@/components/ConfirmDialog.vue'

const props = defineProps<{ ucId: number }>()

const loading = ref(true)
const members = ref<UcMembershipDto[]>([])
const people = ref<PersonDto[]>([])

const headers = [
  { title: 'ID', key: 'personId', value: 'personId', sortable: true },
  { title: 'Nome', key: 'personName', value: 'personName', sortable: true },
  { title: 'IST ID', key: 'istId', value: 'istId', sortable: true },
  { title: 'Email', key: 'email', value: 'email', sortable: true },
  { title: 'Categoria', key: 'role', value: 'role', sortable: true },
  { title: 'Ações', key: 'actions', value: 'actions', sortable: false }
]

onMounted(async () => {
  await getMembers()
  people.value = await RemoteService.getPeople()
})

async function getMembers() {
  members.value = await RemoteService.getUcMembers(props.ucId)
  loading.value = false
}

const addDialog = ref(false)
const roleMappings: Record<string, string> = {
  Aluno: 'ALUNO',
  'Professor Assistente': 'ASSISTENTE'
}
const selectedRole = ref('')
const newMember = ref<AddMembershipDto>({ personId: undefined, role: '' })

const filteredPeople = computed(() => {
  if (selectedRole.value === 'Aluno') {
    return people.value.filter((p) => p.type === 'STUDENT')
  } else if (selectedRole.value === 'Professor Assistente') {
    return people.value.filter((p) => p.type === 'TEACHING_ASSISTANT')
  }
  return []
})

watch(selectedRole, () => {
  newMember.value.personId = undefined
})

const selectedPersonIds = ref<number[]>([])

const saveMember = async () => {
  const role = roleMappings[selectedRole.value]
  for (const personId of selectedPersonIds.value) {
    await RemoteService.addUcMember(props.ucId, { personId, role })
  }
  selectedPersonIds.value = []
  selectedRole.value = ''
  await getMembers()
}

const removeDialogOpen = ref(false)
const memberToRemove = ref<UcMembershipDto | null>(null)

const promptRemove = (member: UcMembershipDto) => {
  memberToRemove.value = member
  removeDialogOpen.value = true
}

const confirmRemove = async () => {
  await RemoteService.removeUcMember(props.ucId, memberToRemove.value!.personId!)
  await getMembers()
}
</script>