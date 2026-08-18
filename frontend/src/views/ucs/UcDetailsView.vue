<template>
  <div v-if="uc">
    <h2 class="text-left ml-1">{{ uc.code }} - {{ uc.name }}</h2>
    <p class="text-left ml-1">
      Regente: {{ uc.regenteName }} | Semestre: {{ uc.semester }} | ECTS: {{ uc.ects }}
    </p>

    <v-tabs v-model="activeTab">
      <v-tab value="members">Membros</v-tab>
      <v-tab value="tests">Testes</v-tab>
      <v-tab value="projects">Projetos</v-tab>
    </v-tabs>

    <v-window v-model="activeTab">
      <v-window-item value="members">
        <UcMembersTab :ucId="Number(id)" />
      </v-window-item>
      <v-window-item value="tests">
        <p class="pa-4">TODO: Tests tab</p>
      </v-window-item>
      <v-window-item value="projects">
        <p class="pa-4">TODO: Projects tab</p>
      </v-window-item>
    </v-window>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import type UcDto from '@/models/ucs/UcDto'
import RemoteService from '@/services/RemoteService'
import UcMembersTab from './UcMembersTab.vue'

const props = defineProps<{ id: string }>()
const uc = ref<UcDto | null>(null)
const activeTab = ref('members')

onMounted(async () => {
  uc.value = await RemoteService.getUc(Number(props.id))
})
</script>