export default class CreateProjectGroupDto {
  memberIds?: number[]
  constructor(obj?: Partial<CreateProjectGroupDto>) { Object.assign(this, obj) }
}