export default class StudentGradesDto {
  personId?: number
  personName?: string
  ucId?: number
  ucName?: string
  grades?: any[]  
  average?: number | null

  constructor(obj?: Partial<StudentGradesDto>) {
    Object.assign(this, obj)
  }
}