import {Documentation} from '../../documentation/models/Documentation';
import {Purpose} from '../../data-sharing-agreement/models/Purpose';
export class Dpa {
  uuid: string;
  name: string;
  description: string;
  derivation: string;
  publisherInformation: string;
  publisherContractInformation: string;
  publisherDataset: string;
  dsaStatusId: number;
  returnToSenderPolicy: string;
  startDate: string;
  endDate: string;
  dataFlows: { [key: string]: string; };
  cohorts: { [key: string]: string; };
  dataSets: { [key: string]: string; };
  publishers: { [key: string]: string; };
  documentations: Documentation[];
  purposes: Purpose[];
  benefits: Purpose[];

  getDisplayItems(): any[] {
      return [
          {label: 'Description', property: 'description'},
          {label: 'Derivation', property: 'derivation'},
          {label: 'Publisher Information', property: 'publisherInformation'},
          {label: 'Publisher Contract Information', property: 'publisherContractInformation'},
          {label: 'Publisher DataSet', property: 'publisherDataSet'},
          {label: 'DSA Status Id', property: 'dsaStatusId'},
          {label: 'Return To Sender Policy', property: 'returnToSenderPolicy'}
      ];
  }
}
