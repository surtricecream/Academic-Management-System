<template>
  <div>
    <h2 class="text-left ml-1">Olá, {{ authStore.name }}!</h2>
    <p class="text-left ml-1 text-medium-emphasis">{{ roleLabel }}</p>

    <v-row class="mt-6">
      <v-col cols="12" sm="6" md="4" v-for="link in quickLinks" :key="link.path">
        <v-card
          class="pa-4"
          :to="link.path"
          hover
        >
          <v-icon :icon="link.icon" size="32" class="mb-2"></v-icon>
          <h3>{{ link.title }}</h3>
          <p class="text-medium-emphasis">{{ link.description }}</p>
        </v-card>
      </v-col>
    </v-row>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

const roleLabels: Record<string, string> = {
  ADMINISTRATOR: 'Administrador',
  MAIN_TEACHER: 'Professor Regente',
  TEACHING_ASSISTANT: 'Professor Assistente',
  STUDENT: 'Aluno'
}
const roleLabel = computed(() => roleLabels[authStore.type ?? ''] ?? '')

const quickLinks = computed(() => {
  const links = [
    { title: 'UCs', description: 'Consultar Unidades Curriculares', path: '/ucs', icon: 'mdi-book-open-variant' }
  ]

  if (authStore.isAdministrator) {
    links.push(
      { title: 'Pessoas', description: 'Gerir pessoas do sistema', path: '/people', icon: 'mdi-account-group' },
      { title: 'Cursos', description: 'Gerir cursos', path: '/courses', icon: 'mdi-school' }
    )
  }

  if (authStore.isStudent) {
    links.push({ title: 'O Meu Perfil', description: 'Ver notas e avaliações pendentes', path: '/my-profile', icon: 'mdi-account-circle' })
  }

  return links
})
</script>