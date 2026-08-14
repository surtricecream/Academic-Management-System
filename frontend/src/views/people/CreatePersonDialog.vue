<template>
  <div class="pa-4 text-center">
    <v-dialog v-model="dialog" max-width="400">
      <template v-slot:activator="{ props: activatorProps }">
        <v-btn
          class="text-none font-weight-regular"
          prepend-icon="mdi-plus"
          text="Adicionar Pessoa"
          v-bind="activatorProps"
          color="primary"
        ></v-btn>
      </template>

      <v-card prepend-icon="mdi-account" title="Nova Pessoa">
        <v-card-text>
          <v-text-field label="Nome*" required v-model="newPerson.name"></v-text-field>
          <v-text-field label="IST ID*" placeholder="ist1000000" required v-model="newPerson.istId"></v-text-field>
          <v-text-field label="Email*" placeholder="nome@tecnico.ulisboa.pt" required v-model="newPerson.email"></v-text-field>
          <v-text-field
            label="Password*"
            required
            type="password"
            v-model="newPerson.password"
          ></v-text-field>
          <v-select
            :items="['Administrador', 'Professor Regente', 'Professor Assistente', 'Aluno']"
            label="Categoria*"
            required
            v-model="selectedType"
          ></v-select>
        </v-card-text>

        <v-divider></v-divider>

        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn text="Close" variant="plain" @click="dialog = false"></v-btn>
          <v-btn
            color="primary"
            text="Save"
            variant="tonal"
            @click="
              dialog = false,
              savePerson()
            "
          ></v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import type CreatePersonDto from '@/models/people/CreatePersonDto'
import RemoteService from '@/services/RemoteService'

const dialog = ref(false)
const emit = defineEmits(['person-created'])

const typeMappings = {
  Administrador: 'ADMINISTRATOR',
  'Professor Regente': 'MAIN_TEACHER',
  'Professor Assistente': 'TEACHING_ASSISTANT',
  Aluno: 'STUDENT'
}

const selectedType = ref('')

const newPerson = ref<CreatePersonDto>({
  name: '',
  istId: '',
  email: '',
  password: '',
  type: ''
})

const savePerson = async () => {
  newPerson.value.type = typeMappings[selectedType.value as keyof typeof typeMappings]
  await RemoteService.createPerson(newPerson.value)
  newPerson.value = { name: '', istId: '', email: '', password: '', type: '' }
  selectedType.value = ''
  emit('person-created')
}
</script>