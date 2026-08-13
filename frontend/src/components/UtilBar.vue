<template>
  <v-app-bar color="secondary" :height="36" class="px-2">
    <v-toolbar-items>
      <v-btn
        href="https://dei.tecnico.ulisboa.pt/"
        selected-class="no-active"
        class="dei-title"
        size="small"
      >
        Departamento de Engenharia Informática
      </v-btn>
    </v-toolbar-items>
    <v-spacer />
    <span v-if="authStore.isLoggedIn">{{ authStore.name }} ({{ authStore.type }})</span>
    <span v-else>Not logged in</span>
    <v-spacer />
    <v-toolbar-items class="align-center">
      <DarkModeSwitch />
    </v-toolbar-items>

    <v-toolbar-items class="ms-2" v-if="authStore.isLoggedIn">
      <v-btn size="small" variant="text" @click="handleLogout">
        Terminar sessão
        <v-icon size="small" class="ms-1" icon="mdi-logout"></v-icon>
      </v-btn>
    </v-toolbar-items>
  </v-app-bar>
</template>

<script setup lang="ts">
import DarkModeSwitch from './DarkModeSwitch.vue'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'

const authStore = useAuthStore()
const router = useRouter()

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.dei-title:hover {
  background-color: transparent !important;
}
</style>