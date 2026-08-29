export default class CreateReviewRequestDto {
  testId?: number
  justification?: string
  deadline?: string
  constructor(obj?: Partial<CreateReviewRequestDto>) { Object.assign(this, obj) }
}