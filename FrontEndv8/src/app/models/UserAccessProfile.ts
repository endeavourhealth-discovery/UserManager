import {ApplicationPolicyAttribute} from "./ApplicationPolicyAttribute";

export class UserAccessProfile {
  applicationName: string;
  applicationId: string;
  canAccessData: boolean;
  roleTypeAccessProfiles: ApplicationPolicyAttribute[];
}
