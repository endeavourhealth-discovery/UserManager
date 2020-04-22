import {ApplicationPolicyAttribute} from "./ApplicationPolicyAttribute";

export class UserProject_local {
	id: string;
	userId: string;
	projectId :string;
	projectName: string;
	organisationId: string;
	organisationName: string;
	deleted: boolean;
  default: boolean;
  applicationPolicyAttributes: ApplicationPolicyAttribute[];


}
