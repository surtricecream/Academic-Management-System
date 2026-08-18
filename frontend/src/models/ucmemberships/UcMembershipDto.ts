export default class UcMembershipDto {
  id?: number
  personId?: number
  personName?: string
  istId?: string
  email?: string
  role?: string

  constructor(obj?: Partial<UcMembershipDto>) {
    Object.assign(this, obj)
  }
}