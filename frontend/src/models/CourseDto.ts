export default class CourseDto {
  id?:number
  code?: string
  name?: string
  durationYears?: number

  constructor(obj?: Partial<CourseDto>) {
    Object.assign(this, obj)
  }
}