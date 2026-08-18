export default class AddMembershipDto {
  personId?: number
  role?: string

  constructor(obj?: Partial<AddMembershipDto>) {
    Object.assign(this, obj)
  }
}