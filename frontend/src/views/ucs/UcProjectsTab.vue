<template>
  <v-row align="center">
    <v-col>
      <h3 class="text-left ml-1">Projetos</h3>
    </v-col>
    <v-col cols="auto">
      <div class="pa-4 text-center">
        <v-dialog v-model="createDialog" max-width="450">
          <template v-slot:activator="{ props: activatorProps }">
            <v-btn
              class="text-none font-weight-regular"
              prepend-icon="mdi-plus"
              text="Adicionar Projeto"
              v-bind="activatorProps"
              color="primary"
            ></v-btn>
          </template>

          <v-card prepend-icon="mdi-folder-edit" title="Novo Projeto">
            <v-card-text>
              <v-text-field label="Título*" required v-model="newProject.title"></v-text-field>
              <v-text-field label="Prazo*" required type="date" :min="today" v-model="newProject.deadline"></v-text-field>
              <v-text-field
                label="Peso* (0.05 a 1)"
                placeholder="Ex: 0.3"
                required
                type="number"
                step="0.01"
                min="0.05"
                max="1"
                v-model.number="newProject.weight"
              ></v-text-field>
              <v-btn-toggle v-model="newProject.isGroupProject" mandatory color="primary" class="mb-4">
                <v-btn :value="false">Individual</v-btn>
                <v-btn :value="true">Grupo</v-btn>
              </v-btn-toggle>
              <v-text-field
                v-if="newProject.isGroupProject"
                label="Tamanho máximo do grupo*"
                required
                type="number"
                min="2"
                v-model.number="newProject.maxGroupSize"
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
                  saveProject()
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
    :items="projects"
    :loading="loading"
    item-key="id"
    class="text-left"
    no-data-text="Sem projetos a apresentar."
  >
    <template v-slot:[`item.isGroupProject`]="{ item }">
      <v-chip v-if="item.isGroupProject" color="blue" text-color="white">Grupo (max {{ item.maxGroupSize }})</v-chip>
      <v-chip v-else color="grey" text-color="white">Individual</v-chip>
    </template>
    <template v-slot:[`item.actions`]="{ item }">
      <v-icon @click="editProject(item)" class="mr-2">mdi-pencil</v-icon>
      <v-icon @click="promptDelete(item)">mdi-delete</v-icon>
    </template>

    <template v-slot:[`item.actions`]="{ item }">
      <v-icon v-if="item.isGroupProject" @click="openGroups(item)" class="mr-2">mdi-account-group</v-icon>
      <v-icon @click="editProject(item)" class="mr-2">mdi-pencil</v-icon>
      <v-icon @click="promptDelete(item)">mdi-delete</v-icon>
    </template>
  </v-data-table>


  <v-dialog v-model="editDialog" max-width="450">
    <v-card prepend-icon="mdi-pencil" title="Editar Projeto">
      <v-card-text>
        <v-text-field label="Título*" required v-model="editingProject.title"></v-text-field>
        <v-text-field label="Prazo*" required type="date" :min="today" v-model="editingProject.deadline"></v-text-field>
        <v-text-field
          label="Peso* (0.05 a 1)"
          required
          type="number"
          step="0.01"
          min="0.05"
          max="1"
          v-model.number="editingProject.weight"
        ></v-text-field>
        <v-checkbox label="Projeto em grupo" v-model="editingProject.isGroupProject"></v-checkbox>
        <v-text-field
          v-if="editingProject.isGroupProject"
          label="Tamanho máximo do grupo*"
          required
          type="number"
          min="2"
          v-model.number="editingProject.maxGroupSize"
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
    title="Eliminar Projeto"
    :message="`Tem a certeza que quer eliminar '${projectToDelete?.title}'?`"
    @confirm="confirmDelete"
  />

  <ProjectGroupsDialog
    v-model="groupsDialogOpen"
    :projectId="selectedProject?.id ?? 0"
    :projectTitle="selectedProject?.title ?? ''"
    :ucId="props.ucId"
  />
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import type ProjectDto from '@/models/evaluation/ProjectDto'
import type CreateProjectDto from '@/models/evaluation/CreateProjectDto'
import RemoteService from '@/services/RemoteService'
import ConfirmDialog from '@/components/ConfirmDialog.vue'
import ProjectGroupsDialog from './ProjectGroupsDialog.vue'

const props = defineProps<{ ucId: number }>()

const loading = ref(true)
const projects = ref<ProjectDto[]>([])
const today = new Date().toISOString().split('T')[0]

const headers = [
  { title: 'ID', key: 'id', value: 'id', sortable: true },
  { title: 'Título', key: 'title', value: 'title', sortable: true },
  { title: 'Prazo', key: 'deadline', value: 'deadline', sortable: true },
  { title: 'Peso', key: 'weight', value: 'weight', sortable: true },
  { title: 'Tipo', key: 'isGroupProject', value: 'isGroupProject', sortable: false },
  { title: 'Ações', key: 'actions', value: 'actions', sortable: false }
]

onMounted(getProjects)

async function getProjects() {
  projects.value = await RemoteService.getProjects(props.ucId)
  loading.value = false
}

const createDialog = ref(false)
const newProject = ref<CreateProjectDto>({
  title: '',
  deadline: '',
  weight: undefined,
  isGroupProject: false,
  maxGroupSize: undefined,
  ucId: props.ucId
})

const groupsDialogOpen = ref(false)
const selectedProject = ref<ProjectDto | null>(null)

const openGroups = (project: ProjectDto) => {
  selectedProject.value = project
  groupsDialogOpen.value = true
}

const saveProject = async () => {
  await RemoteService.createProject(props.ucId, newProject.value)
  newProject.value = { title: '', deadline: '', weight: undefined, isGroupProject: false, maxGroupSize: undefined, ucId: props.ucId }
  await getProjects()
}

const editDialog = ref(false)
const editingProject = ref<CreateProjectDto & { id?: number }>({})

const editProject = (project: ProjectDto) => {
  editingProject.value = {
    id: project.id,
    title: project.title,
    deadline: project.deadline,
    weight: project.weight,
    isGroupProject: project.isGroupProject,
    maxGroupSize: project.maxGroupSize,
    ucId: props.ucId
  }
  editDialog.value = true
}

const saveEdit = async () => {
  await RemoteService.updateProject(props.ucId, editingProject.value.id!, editingProject.value)
  editDialog.value = false
  await getProjects()
}

const deleteDialogOpen = ref(false)
const projectToDelete = ref<ProjectDto | null>(null)

const promptDelete = (project: ProjectDto) => {
  projectToDelete.value = project
  deleteDialogOpen.value = true
}

const confirmDelete = async () => {
  await RemoteService.deleteProject(props.ucId, projectToDelete.value!.id!)
  await getProjects()
}
</script>