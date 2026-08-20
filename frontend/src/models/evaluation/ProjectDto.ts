export default class ProjectDto {
  id?: number
  title?: string
  deadline?: string
  weight?: number
  isGroupProject?: boolean
  maxGroupSize?: number
  ucId?: number
  ucName?: string
  constructor(obj?: Partial<ProjectDto>) { Object.assign(this, obj) }
}