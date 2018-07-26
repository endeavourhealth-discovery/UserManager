export class DelegationData {
  uuid : string;
  name : string;
  createSuperUsers : boolean;
  createUsers : boolean;
  children : DelegationData[];
}
