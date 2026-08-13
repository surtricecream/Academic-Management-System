import { defineStore } from 'pinia'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: null as string | null,
    personId: null as number | null,
    name: null as string | null,
    type: null as string | null,
  }),
  getters: {
    isLoggedIn(): boolean {
      return this.token !== null
    },
    isAdministrator(): boolean {
      return this.type === 'ADMINISTRATOR'
    },
    isMainTeacher(): boolean {
      return this.type === 'MAIN_TEACHER'
    },
    isTeachingAssistant(): boolean {
      return this.type === 'TEACHING_ASSISTANT'
    },
    isStudent(): boolean {
      return this.type === 'STUDENT'
    },
  },
  actions: {
    login(token: string, personId: number, name: string, type: string) {
      this.token = token
      this.personId = personId
      this.name = name
      this.type = type
    },
    logout() {
      this.token = null
      this.personId = null
      this.name = null
      this.type = null
    },
  },
  persist: true,
})