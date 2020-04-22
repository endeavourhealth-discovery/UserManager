export class ApplicationPolicyAttribute {
  id : string;
  name: string;
  application: string;
  applicationId: string;
  applicationPolicyId : string;
  applicationAccessProfileId : string;
  applicationAccessProfileName : string;
  applicationAccessProfileDescription : string;
  applicationAccessProfileSuperUser : boolean;
  isDeleted: boolean;

  getDisplayItems(): any[] {
    return [
      {label: 'Application name', property: 'application'},
      {label: 'Profile name', property: 'applicationAccessProfileName'}
    ];
  }
}
