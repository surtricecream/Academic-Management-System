export default class ReviewRequestDto {
  id?: number
  testId?: number
  testTitle?: string
  studentId?: number
  studentName?: string
  justification?: string
  deadline?: string
  status?: string
  assistantOpinion?: string
  assistantName?: string
  regenteDecision?: string
  decidedByName?: string
  createdAt?: string
  decidedAt?: string
  constructor(obj?: Partial<ReviewRequestDto>) { Object.assign(this, obj) }
}