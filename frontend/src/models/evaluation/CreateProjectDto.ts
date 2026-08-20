export default class CreateProjectDto {
  title?: string
  deadline?: string
  weight?: number
  isGroupProject?: boolean
  maxGroupSize?: number
  ucId?: number
  constructor(obj?: Partial<CreateProjectDto>) { Object.assign(this, obj) }
}