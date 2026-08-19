export default class CreateTestDto {
  title?: string
  date?: string
  weight?: number
  ucId?: number

  constructor(obj?: Partial<CreateTestDto>) {
    Object.assign(this, obj)
  }
}