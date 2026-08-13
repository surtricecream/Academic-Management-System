export default class CreateUcDto {
  code?: string
  name?: string
  semester?: number
  ects?: number
  regenteName?: number
  courseNames?: number[]

  constructor(obj?: Partial<CreateUcDto>) {
    Object.assign(this, obj)
  }
}