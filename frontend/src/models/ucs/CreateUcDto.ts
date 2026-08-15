export default class CreateUcDto {
  code?: string
  name?: string
  semester?: number
  ects?: number
  regenteId?: number
  courseIds?: number[]

  constructor(obj?: Partial<CreateUcDto>) {
    Object.assign(this, obj)
  }
}