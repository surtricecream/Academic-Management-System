export default class UcDto {
  id?: number
  code?: string
  name?: string
  semester?: number
  ects?: number
  regenteId?: number
  regenteName?: string
  courseIds?: number[]
  courseNames?: string[]

  constructor(obj?: Partial<UcDto>) {
    Object.assign(this, obj)
  }
}