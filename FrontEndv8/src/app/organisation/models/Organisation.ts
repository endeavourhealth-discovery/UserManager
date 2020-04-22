import {Address} from './Address';

export class Organisation {
    uuid: string;
    name: string;
    createUsers: boolean;
    createSuperUsers: boolean;
    alternativeName: string;
    odsCode: string;
    icoCode: string;
    igToolkitStatus: string;
    dateOfRegistration: string;
    registrationPerson: string;
    evidenceOfRegistration: string;
    isService: number;
    bulkImported: number;
    bulkItemUpdated: number;
    bulkConflictedWith: string;
    type: string;
    regions: { [key: string]: string; };
    parentOrganisations: { [key: string]: string; };
    childOrganisations: { [key: string]: string; };
    services: { [key: string]: string; };
    dpaPublishing: { [key: string]: string; };
    dsaPublishing: { [key: string]: string; };
    dsaSubscribing: { [key: string]: string; };
    addresses: Address[];

  getDisplayItems(): any[] {
    return [
      {label: 'Name', property: 'name', secondary: false},
      {label: 'ODS code', property: 'odsCode', secondary: false}
    ];
  }
}
