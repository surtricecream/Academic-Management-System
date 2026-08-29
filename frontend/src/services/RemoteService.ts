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
import type UcMembershipDto from '@/models/ucmemberships/UcMembershipDto'
import type AddMembershipDto from '@/models/ucmemberships/AddMembershipDto'
import type TestDto from '@/models/evaluation/TestDto'
import type CreateTestDto from '@/models/evaluation/CreateTestDto'
import type ProjectDto from '@/models/evaluation/ProjectDto'
import type CreateProjectDto from '@/models/evaluation/CreateProjectDto'
import type ProjectGroupDto from '@/models/evaluation/ProjectGroupDto'
import type CreateProjectGroupDto from '@/models/evaluation/CreateProjectGroupDto'
import type CreateGradeDto from '@/models/evaluation/CreateGradeDto'
import type StudentGradesDto from '@/models/evaluation/StudentGradesDto'
import type StudentProfileDto from '@/models/evaluation/StudentProfileDto'

const httpClient = axios.create()
httpClient.defaults.timeout = 50000
httpClient.defaults.baseURL = import.meta.env.VITE_ROOT_API
httpClient.defaults.headers.post['Content-Type'] = 'application/json'

export default class RemoteServices {

  // People
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

  // Courses
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

  // Ucs
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

  // Memberships
  static async getUcMembers(ucId: number): Promise<UcMembershipDto[]> {
    return httpClient.get(`/ucs/${ucId}/members`)
  }

  static async addUcMember(ucId: number, dto: AddMembershipDto): Promise<UcMembershipDto> {
    return httpClient.post(`/ucs/${ucId}/members`, dto)
  }

  static async removeUcMember(ucId: number, personId: number): Promise<void> {
    return httpClient.delete(`/ucs/${ucId}/members/${personId}`)
  }

  // Tests
  static async getTests(ucId: number): Promise<TestDto[]> {
    return httpClient.get(`/ucs/${ucId}/tests`)
  }

  static async createTest(ucId: number, dto: CreateTestDto): Promise<TestDto> {
    return httpClient.post(`/ucs/${ucId}/tests`, dto)
  }

  static async updateTest(ucId: number, testId: number, dto: CreateTestDto): Promise<TestDto> {
    return httpClient.put(`/ucs/${ucId}/tests/${testId}`, dto)
  }

  static async deleteTest(ucId: number, testId: number): Promise<void> {
    return httpClient.delete(`/ucs/${ucId}/tests/${testId}`)
  }

  // Projects
  static async getProjects(ucId: number): Promise<ProjectDto[]> {
    return httpClient.get(`/ucs/${ucId}/projects`)
  }

  static async createProject(ucId: number, dto: CreateProjectDto): Promise<ProjectDto> {
    return httpClient.post(`/ucs/${ucId}/projects`, dto)
  }

  static async updateProject(ucId: number, projectId: number, dto: CreateProjectDto): Promise<ProjectDto> {
    return httpClient.put(`/ucs/${ucId}/projects/${projectId}`, dto)
  }

  static async deleteProject(ucId: number, projectId: number): Promise<void> {
    return httpClient.delete(`/ucs/${ucId}/projects/${projectId}`)
  }

  // Group Projects
  static async getGroups(projectId: number): Promise<ProjectGroupDto[]> {
    return httpClient.get(`/projects/${projectId}/groups`)
  }

  static async createGroup(projectId: number, dto: CreateProjectGroupDto): Promise<ProjectGroupDto> {
    return httpClient.post(`/projects/${projectId}/groups`, dto)
  }

  static async autoAssignGroups(projectId: number): Promise<ProjectGroupDto[]> {
    return httpClient.post(`/projects/${projectId}/groups/auto-assign`, {})
  }

  static async deleteGroup(projectId: number, groupId: number): Promise<void> {
    return httpClient.delete(`/projects/${projectId}/groups/${groupId}`)
  }

  // Grades
  static async gradeTest(testId: number, dto: CreateGradeDto): Promise<any> {
    return httpClient.post(`/api/tests/${testId}/grade`, dto)
  }

  static async gradeIndividualProject(projectId: number, dto: CreateGradeDto): Promise<any> {
    return httpClient.post(`/api/projects/${projectId}/grade/individual`, dto)
  }

  static async gradeGroupProject(projectId: number, dto: CreateGradeDto): Promise<any> {
    return httpClient.post(`/api/projects/${projectId}/grade/group`, dto)
  }

  static async getStudentGrades(ucId: number, personId: number): Promise<StudentGradesDto> {
    return httpClient.get(`/api/ucs/${ucId}/students/${personId}/grades`)
  }
  
  // Student Profile
  static async getMyProfile(personId: number): Promise<StudentProfileDto> {
    return httpClient.get(`/api/students/${personId}/profile`)
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