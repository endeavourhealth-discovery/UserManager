import {RoleTypeAccessProfile} from "../../configuration/models/RoleTypeAccessProfile";

export class UserAccessProfile {
  applicationName: string;
  applicationId: string;
  canAccessData: boolean;
  roleTypeAccessProfiles: RoleTypeAccessProfile[];
}
