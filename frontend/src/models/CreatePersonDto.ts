export default class CreatePersonDto {
  name?: string
  istId?: string
  email?: string
  type?: string
  password?: string

  constructor(obj?: Partial<CreatePersonDto>) {
    Object.assign(this, obj)
  }
}