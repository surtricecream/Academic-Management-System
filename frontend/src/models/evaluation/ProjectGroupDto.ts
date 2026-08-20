export default class ProjectGroupDto {
  id?: number
  projectId?: number
  projectTitle?: string
  memberNames?: string[]
  memberIds?: number[]
  constructor(obj?: Partial<ProjectGroupDto>) { Object.assign(this, obj) }
}