export class DelegationRelationship {
  parentUuid: string;
  parentType: number;
  childUuid: string;
  childType: number;
  includeAllChildren: number;
  createSuperUsers: boolean;
  createUsers: boolean;
  delegation: string;
}
