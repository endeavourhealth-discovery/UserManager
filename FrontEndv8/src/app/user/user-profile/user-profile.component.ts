import {Component, Input, OnInit} from '@angular/core';
import {Location} from "@angular/common";
import {ConfigurationService} from "../../configuration/configuration.service";
import {DelegationService} from "../../d3-delegation/delegation.service";
import {UserService} from "../user.service";
import {Router} from "@angular/router";
import {User} from "../../models/User";
import {UserProfile} from "../../models/UserProfile";
import {UserProject} from "dds-angular8/lib/user-manager/models/UserProject";
import {LoggerService, UserManagerService} from "dds-angular8";
import {ApplicationPolicyAttribute} from "../../models/ApplicationPolicyAttribute";

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.css']
})
export class UserProfileComponent implements OnInit {

  @Input() user: User;
  userProfile: UserProfile;
  precisCollapsed = true;
  profileLoadingComplete = false;

  public activeProject: UserProject;
  admin = false;
  superUser = false;

  constructor(private log: LoggerService,
              private router: Router,
              private location: Location,
              protected userService: UserService,
              private delegationService: DelegationService,
              private configurationService: ConfigurationService,
              private userManagerService: UserManagerService) {

    let s = this.router.getCurrentNavigation().extras.state;

    if (s == null) {
      this.user = {} as User;
      this.router.navigate(['user']);
      return;
    }
    this.user = s.user;
  }

  ngOnInit() {

    this.userManagerService.onProjectChange.subscribe(active => {
      this.activeProject = active;
      this.roleChanged();
    });

    this.refresh(false);
  }

  roleChanged() {

    if (this.activeProject.applicationPolicyAttributes.find(x => x.applicationAccessProfileName == 'Super User') != null) {
      this.admin = true;
      this.superUser = true;
    } else if (this.activeProject.applicationPolicyAttributes.find(x => x.applicationAccessProfileName == 'Admin') != null) {
      this.admin = true;
      this.superUser = false;
    } else {
      this.admin = false;
      this.superUser = false;
    }
  }

  close() {
    this.location.back();
  }

  loadRoleProfile() {

    this.profileLoadingComplete = false;
    this.userService.getUserProfile(this.user.uuid)
      .subscribe(
        (result) => {
          this.userProfile = result;
          this.profileLoadingComplete = true;
        },
        (error) => this.log.error('Error loading user profiles')
      );
  }

  refresh(refresh : boolean = false) {

    this.flushCache();
  }

  flushCache(){

    this.configurationService.flushCache()
      .subscribe(
        (result) => {
          this.loadRoleProfile();
        },
        (error) => this.log.error('Flushing cache failed. Please try again.')
      );
  }

  getStringListFromArray(dataArray: any) {

    var stringArray = []

    for (let idx in dataArray) {
      stringArray.push(dataArray[idx]);
    }

    return stringArray.toString();

  }

  getApplicationAccess(attributes: ApplicationPolicyAttribute[]) {
    let appAttributeMap : Map<string, string[]> = new Map<string, string[]>();

    for (let attribute of attributes) {
      let appAtt : string[] = [];
      if (appAttributeMap.has(attribute.application)) {
        appAtt = appAttributeMap.get(attribute.application);
      }
      appAtt.push(attribute.applicationAccessProfileName);
      appAttributeMap.set(attribute.application, appAtt);
    }

    let appString: string = '';
    appAttributeMap.forEach((value, key) => {
      appString += key;
      appString += ' (' + value.toString() + ') '
    });

    return appString;
  }

}
