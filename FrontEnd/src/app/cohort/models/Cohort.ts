export class Cohort {
    uuid : string;
    name:string;
    consentModelId: string;
    description: string;
    dpas : { [key:string]:string; };

    getDisplayItems() :any[] {
        return [
            {label: 'name', property: 'name'}
        ];
    }
}
