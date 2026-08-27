export default class CreateGradeDto {
  testId?: number | null
  projectId?: number | null
  personId?: number | null
  groupId?: number | null
  score?: number
  constructor(obj?: Partial<CreateGradeDto>) { Object.assign(this, obj) }
}