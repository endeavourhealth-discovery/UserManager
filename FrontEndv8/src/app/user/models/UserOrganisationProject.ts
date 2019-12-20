import {Project} from "../../configuration/models/Project";
import {Organisation} from "./Organisation";

export class UserOrganisationProject {
  organisation: Organisation;
  projects: Project[];
}
