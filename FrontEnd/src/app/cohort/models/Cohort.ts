export class Cohort {
    uuid : string;
    name:string;
    nature: string;
    patientCohortInclusionConsentModel: string;
    queryDefinition: string;
    removalPolicy: string;
    dpas : { [key:string]:string; };

    getDisplayItems() :any[] {
        return [
            {label: 'Nature', property: 'nature'}
        ];
    }
}
