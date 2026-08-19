export default class TestDto {
  id?: number
  title?: string
  date?: string
  weight?: number
  ucId?: number
  ucName?: string

  constructor(obj?: Partial<TestDto>) {
    Object.assign(this, obj)
  }
}