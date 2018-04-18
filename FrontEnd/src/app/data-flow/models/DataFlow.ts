import {Documentation} from "../../documentation/models/Documentation";

export class DataFlow {
    uuid: string;
    name: string;
    directionId: number;
    flowScheduleId: number;
    approximateVolume: number;
    dataExchangeMethodId: number;
    storageProtocolId: number;
    securityInfrastructureId: number;
    securityArchitectureId: number;
    flowStatusId: number;
    additionalDocumentation: string;
    signOff: string;
    dsas: { [key: string]: string; };
    dpas: { [key: string]: string; };
    documentations: Documentation[];

    getDisplayItems(): any[] {
        return [
            {label: 'Flow status', property: 'flowStatusId'},
            {label: 'Approximate volume', property: 'approximateVolume'},
            {label: 'Sign off', property: 'signOff'}
        ];
    }
}
