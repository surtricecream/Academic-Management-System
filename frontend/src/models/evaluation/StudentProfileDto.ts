export interface EnrolledUcDto {
  ucId: number
  ucName: string
  average: number | null
}

export interface PendingEvaluationDto {
  type: string
  id: number
  title: string
  deadline: string
  ucName: string
}

export default class StudentProfileDto {
  personId?: number
  personName?: string
  ucs?: EnrolledUcDto[]
  pendingEvaluations?: PendingEvaluationDto[]

  constructor(obj?: Partial<StudentProfileDto>) {
    Object.assign(this, obj)
  }
}