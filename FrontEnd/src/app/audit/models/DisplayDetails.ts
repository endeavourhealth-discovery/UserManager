export class DisplayDetails {

  getRoleDisplayDetails(): any[] {
    return [
      { label: 'Title', property: 'title'},
      { label: 'User id', property: 'userId'},
      { label: 'Role Type', property: 'roleType'},
      { label: 'Organisation', property: 'organisation'},
      { label: 'User access profile', property: 'accessProfile'}
    ]
  }
}
