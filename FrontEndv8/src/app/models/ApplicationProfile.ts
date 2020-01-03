export class ApplicationProfile {
  id: string;
  name: string;
  applicationId: string;
  description: string;
  superUser: boolean;
  isDeleted : boolean;

  getDisplayItems(): any[] {
    return [
      {label: 'Name', property: 'name'},
      {label: 'Description', property: 'description'},
      {label: 'Grants super user access', property: 'superUser'}
    ];
  }
}
