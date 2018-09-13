export class DisplayDetails {

  getRoleDisplayDetails(): any[] {
    return [
      { label: 'User id', property: 'userId'},
      { label: 'Role Type', property: 'roleType'},
      { label: 'Organisation', property: 'organisation'}
    ]
  }

  getUserDisplayDetails(): any[] {
    return [
      { label: 'User id', property: 'id'},
      { label: 'Username', property: 'username'},
      { label: 'Forename', property: 'forename'},
      { label: 'Surname', property: 'surname'},
      { label: 'E-mail', property: 'email'},
      { label: 'Photo URL', property: 'photo'},
      { label: 'Mobile', property: 'mobile'}
    ]
  }

  getDelegationRelationshipDisplayDetails(): any[] {
    return [
      { label: 'Id', property: 'id'},
      { label: 'Delegation', property: 'delegation'},
      { label: 'Parent organisation', property: 'parentOrg'},
      { label: 'Child organisation', property: 'childOrg'},
      { label: 'Create super users', property: 'createSuperUsers'},
      { label: 'Create users', property: 'createUsers'}
    ]
  }

  getDelegationDisplayDetails(): any[] {
    return [
      { label: 'Id', property: 'id'},
      { label: 'Name', property: 'name'},
      { label: 'Root organisation', property: 'rootOrganisation'}
    ]
  }

  getDefaultRoleChangeDisplayDetails(): any[] {
    return [
      { label: 'Id', property: 'id'},
      { label: 'User', property: 'user'},
      { label: 'Role Type', property: 'roleType'},
      { label: 'Organisation', property: 'organisation'}
    ]
  }

  getApplicationDisplayDetails(): any[] {
    return [
      { label: 'Id', property: 'id'},
      { label: 'Name', property: 'name'},
      { label: 'Description', property: 'description'},
      { label: 'Application details', property: 'applicationTree'}
    ]
  }

  getApplicationProfileDisplayDetails(): any[] {
    return [
      { label: 'Id', property: 'id'},
      { label: 'Name', property: 'name'},
      { label: 'Description', property: 'description'},
      { label: 'Application', property: 'applicationName'},
      { label: 'Application profile details', property: 'profileTree'}
    ]
  }

  getRoleTypeAccessProfileDisplayDetails(): any[] {
    return [
      { label: 'Id', property: 'id'},
      { label: 'Role type name', property: 'roleTypeName'},
      { label: 'Application profile name', property: 'applicationProfileName'},
      { label: 'profileTree', property: 'profileTree'}
    ]
  }

  getUserRegionDisplayDetails(): any[] {
    return [
      { label: 'User', property: 'user'},
      { label: 'Region', property: 'region'}
    ]
  }
}
