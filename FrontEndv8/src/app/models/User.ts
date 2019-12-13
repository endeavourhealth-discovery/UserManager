import {UserProject} from "./UserProject";
export class User {

			constructor() {
	}

	uuid:string;
	forename:string;
	surname:string;
	username:string;
	password:string;
	email:string;
	mobile:string;
	photo:string;
	defaultOrgId: string;
  createdTimeStamp: number;
	userProjects: UserProject[];

	isSuperUser:boolean;
	permissions:string[];

	displayName():string {
		if(this.forename == null && this.surname == null) {
			if(this.uuid != null) {
				return this.uuid;
			}
			return 'Unknown User';
		}

		var displayName = this.forename + ' ' + this.surname;

		return displayName.trim();
	}
}
