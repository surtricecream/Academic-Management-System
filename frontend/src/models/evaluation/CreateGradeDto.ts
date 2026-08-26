export default class CreateGradeDto {
  testId?: number
  projectId?: number
  personId?: number
  groupId?: number
  score?: number
  constructor(obj?: Partial<CreateGradeDto>) { Object.assign(this, obj) }
}