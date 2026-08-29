<template>
  <UtilBar />
  <NavBar :navbarItems="navbarItems" />
</template>

<script setup lang="ts">
import UtilBar from '@/components/UtilBar.vue'
import NavBar from '@/components/NavBar.vue'
import { computed } from 'vue'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

const navbarItems = computed(() => {
  const items = [
    { name: 'Início', path: '/', icon: 'mdi-home' },
    { name: 'Pessoas', path: '/people', icon: 'mdi-account-group' },
    { name: 'Cursos', path: '/courses', icon: 'mdi-school' },
    { name: 'UCs', path: '/ucs', icon: 'mdi-book-open-variant' },
    { name: 'Estatísticas', path: '/statistics', icon: 'mdi-chart-bar' }
  ]
  if (authStore.isStudent) {
    items.push({ name: 'O Meu Perfil', path: '/my-profile', icon: 'mdi-account-circle' })
  }
  return items
})
</script>