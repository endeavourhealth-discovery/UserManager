import {Address} from './Address';

export class Organisation {
    uuid: string;
    name: string;
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
            {label: 'ODS code', property: 'odsCode'},
            {label: 'Alternative name', property: 'alternativeName'},
            {label: 'ICO code', property: 'icoCode'},
            {label: 'IG toolkit status', property: 'igToolkitStatus'},
            {label: 'Date of registration', property: 'dateOfRegistration'},
            {label: 'Registration person', property: 'registrationPerson'},
            {label: 'Evidence Of registration', property: 'evidenceOfRegistration'},
            {label: 'Type', property: 'type'}
        ];
    }
}
