<template>
  <div>
    <h1>Login</h1>
    <form @submit.prevent="handleLogin">
      <input v-model="email" type="email" placeholder="Email" required />
      <input v-model="password" type="password" placeholder="Password" required />
      <button type="submit">Login</button>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import RemoteServices from '@/services/RemoteService'

const email = ref('')
const password = ref('')
const router = useRouter()
const authStore = useAuthStore()

async function handleLogin() {
  const response = await RemoteServices.login(email.value, password.value)
  authStore.login(response.token, response.personId, response.name, response.type)
  router.push('/')
}
</script>