import axios from 'axios'
import type { AxiosResponse } from 'axios'
import { useAppearanceStore } from '@/stores/appearance'
import { useAuthStore } from '@/stores/auth'
import DeiError from '@/models/DeiError'
import type PersonDto from '../models/people/PersonDto'
import type CreatePersonDto from '../models/people/CreatePersonDto'
import type CourseDto from '../models/courses/CourseDto'
import type UcDto from '../models/ucs/UcDto'
import type CreateUcDto from '../models/ucs/CreateUcDto'

const httpClient = axios.create()
httpClient.defaults.timeout = 50000
httpClient.defaults.baseURL = import.meta.env.VITE_ROOT_API
httpClient.defaults.headers.post['Content-Type'] = 'application/json'

export default class RemoteServices {
  static async getPeople(): Promise<PersonDto[]> {
    return httpClient.get('/people')
  }

  static async createPerson(person: CreatePersonDto): Promise<PersonDto> {
    return httpClient.post('/people', person)
  }

  static async updatePerson(id: number, person: PersonDto): Promise<PersonDto> {
    return httpClient.put(`/people/${id}`, person)
  }

  static async deletePerson(id: number): Promise<void> {
    return httpClient.delete(`/people/${id}`)
  }

  static async login(email: string, password: string): Promise<any> {
    return httpClient.post('/auth/login', { email, password })
  }

  static async getCourses(): Promise<CourseDto[]> {
  return httpClient.get('/courses')
  }

  static async createCourse(course: CourseDto): Promise<CourseDto> {
    return httpClient.post('/courses', course)
  }

  static async updateCourse(id: number, course: CourseDto): Promise<CourseDto> {
    return httpClient.put(`/courses/${id}`, course)
  }

  static async deleteCourse(id: number): Promise<void> {
    return httpClient.delete(`/courses/${id}`)
  }

  static async getUcs(): Promise<UcDto[]> {
    return httpClient.get('/ucs')
  }

  static async getUc(id: number): Promise<UcDto> {
    return httpClient.get(`/ucs/${id}`)
  }

  static async createUc(uc: CreateUcDto): Promise<UcDto> {
    return httpClient.post('/ucs', uc)
  }

  static async updateUc(id: number, uc: CreateUcDto): Promise<UcDto> {
    return httpClient.put(`/ucs/${id}`, uc)
  }
  
  static async deleteUc(id: number): Promise<void> {
    return httpClient.delete(`/ucs/${id}`)
  }

  static async errorMessage(error: any): Promise<string> {
    if (error.message === 'Network Error') {
      return 'Unable to connect to the server'
    } else if (error.message.split(' ')[0] === 'timeout') {
      return 'Request timeout - Server took too long to respond'
    } else {
      return error.response?.data?.message ?? 'Unknown Error'
    }
  }

  static async handleError(error: any): Promise<never> {
    const deiErr = new DeiError(
      await RemoteServices.errorMessage(error),
      error.response?.data?.code ?? -1
    )
    const appearance = useAppearanceStore()
    appearance.pushError(deiErr)
    appearance.loading = false
    throw deiErr
  }
}

httpClient.interceptors.request.use((config) => {
  const authStore = useAuthStore()
  if (authStore.token) {
    config.headers.Authorization = `Bearer ${authStore.token}`
  }
  return config
}, RemoteServices.handleError)

httpClient.interceptors.response.use((response) => response.data, RemoteServices.handleError)