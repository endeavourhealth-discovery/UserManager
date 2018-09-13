import {Organisation} from "../../organisation/models/Organisation";
import {Project} from "../../configuration/models/Project";

export class UserProfile {
  organisation: Organisation;
  projects: Project[];
}
