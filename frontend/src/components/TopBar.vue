<template>
  <UtilBar />
  <NavBar :navbarItems="navbarItems" />
</template>

<script setup lang="ts">
import UtilBar from '@/components/UtilBar.vue'
import NavBar from '@/components/NavBar.vue'

const navbarItems = [
  { name: 'Início', path: '/', icon: 'mdi-home' },
  { name: 'Pessoas', path: '/people', icon: 'mdi-account-group' },
  { name: 'Cursos', path: '/courses', icon: 'mdi-school' },
  { name: 'UCs', path: '/ucs', icon: 'mdi-book-open-variant' },
  { name: 'Estatísticas', path: '/statistics', icon: 'mdi-chart-bar' }
]
</script>