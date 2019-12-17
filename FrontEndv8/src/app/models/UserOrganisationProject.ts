import {Project} from "../models/Project";
import {Organisation} from "../models/Organisation";

export class UserOrganisationProject {
  organisation: Organisation;
  projects: Project[];
}
