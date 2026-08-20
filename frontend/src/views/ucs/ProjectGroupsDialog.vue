<template>
  <v-dialog v-model="dialogOpen" max-width="600">
    <v-card prepend-icon="mdi-account-group" :title="`Grupos - ${projectTitle}`">
      <v-card-text>
        <v-row align="center" class="mb-2">
          <v-col cols="auto">
            <v-btn size="small" color="primary" @click="showCreateForm = !showCreateForm">
              Criar Grupo Manualmente
            </v-btn>
          </v-col>
          <v-col cols="auto">
            <v-btn size="small" color="secondary" @click="autoAssign">
              Atribuir Automaticamente
            </v-btn>
          </v-col>
        </v-row>

        <div v-if="showCreateForm" class="mb-4">
          <v-select
            :items="students"
            item-title="name"
            item-value="id"
            label="Membros*"
            multiple
            required
            v-model="newGroupMemberIds"
          ></v-select>
          <v-btn size="small" color="primary" @click="createGroup">Guardar Grupo</v-btn>
        </div>

        <v-list>
          <v-list-item v-for="group in groups" :key="group.id">
            <v-list-item-title>
              Grupo {{ group.id }}: {{ group.memberNames?.join(', ') }}
            </v-list-item-title>
            <template v-slot:append>
              <v-icon @click="promptDelete(group)">mdi-delete</v-icon>
            </template>
          </v-list-item>
        </v-list>
      </v-card-text>

      <v-divider></v-divider>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn text="Close" variant="plain" @click="dialogOpen = false"></v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>

  <ConfirmDialog
    v-model="deleteDialogOpen"
    title="Eliminar Grupo"
    :message="`Tem a certeza que quer eliminar este grupo?`"
    @confirm="confirmDelete"
  />
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import type ProjectGroupDto from '@/models/evaluation/ProjectGroupDto'
import type PersonDto from '@/models/people/PersonDto'
import RemoteService from '@/services/RemoteService'
import ConfirmDialog from '@/components/ConfirmDialog.vue'

const props = defineProps<{
  modelValue: boolean
  projectId: number
  projectTitle: string
  ucId: number
}>()
const emit = defineEmits(['update:modelValue'])

const dialogOpen = ref(props.modelValue)
watch(() => props.modelValue, (v) => {
  dialogOpen.value = v
  if (v) loadData()
})
watch(dialogOpen, (v) => emit('update:modelValue', v))

const groups = ref<ProjectGroupDto[]>([])
const students = ref<PersonDto[]>([])

async function loadData() {
  groups.value = await RemoteService.getGroups(props.projectId)
  const members = await RemoteService.getUcMembers(props.ucId)
  students.value = members
    .filter((m) => m.role === 'ALUNO')
    .map((m) => ({ id: m.personId, name: m.personName }))
}

const showCreateForm = ref(false)
const newGroupMemberIds = ref<number[]>([])

const createGroup = async () => {
  await RemoteService.createGroup(props.projectId, { memberIds: newGroupMemberIds.value })
  newGroupMemberIds.value = []
  showCreateForm.value = false
  await loadData()
}

const autoAssign = async () => {
  await RemoteService.autoAssignGroups(props.projectId)
  await loadData()
}

const deleteDialogOpen = ref(false)
const groupToDelete = ref<ProjectGroupDto | null>(null)

const promptDelete = (group: ProjectGroupDto) => {
  groupToDelete.value = group
  deleteDialogOpen.value = true
}

const confirmDelete = async () => {
  await RemoteService.deleteGroup(props.projectId, groupToDelete.value!.id!)
  await loadData()
}
</script>