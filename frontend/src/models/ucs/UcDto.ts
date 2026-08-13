export default class UcDto {
  id?: number
  code?: string
  name?: string
  semester?: number
  ects?: number
  regenteName?: string
  courseNames?: string[]

  constructor(obj?: Partial<UcDto>) {
    Object.assign(this, obj)
  }
}