import {Project} from "../../configuration/models/Project";
import {Organisation} from "../../organisation/models/Organisation";

export class UserOrganisationProject {
  organisation: Organisation;
  projects: Project[];
}
