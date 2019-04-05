import {Component, Input, OnInit} from '@angular/core';
import {User} from "../models/User";
import {UserProject} from "../models/UserProject";
import {Location} from "@angular/common";
import {ConfigurationService} from "../../configuration/configuration.service";
import {DelegationService} from "../../d3-delegation/delegation.service";
import {LoggerService, UserManagerNotificationService, UserManagerService} from "eds-angular4";
import {UserService} from "../user.service";
import {Router} from "@angular/router";
import {ModuleStateService} from "eds-angular4/dist/common";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {UserProfile} from "../models/UserProfile";
import {ApplicationPolicyAttribute} from "../../configuration/models/ApplicationPolicyAttribute";

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
              private $modal: NgbModal,
              private router: Router,
              private location: Location,
              protected userService: UserService,
              private delegationService: DelegationService,
              private configurationService: ConfigurationService,
              private state: ModuleStateService,
              private userManagerNotificationService: UserManagerNotificationService) { }

  ngOnInit() {
    let vm = this;

    let s = this.state.getState('userProfile');
    if (s == null) {
      this.user = {} as User;
      this.router.navigate(['user']);
      return;
    }
    this.user = Object.assign( {}, s.user);

    vm.userManagerNotificationService.activeUserProject.subscribe(active => {
      vm.activeProject = active;
      vm.roleChanged();
    });

    vm.refresh(false);
  }

  roleChanged() {
    const vm = this;
    if (vm.activeProject.applicationPolicyAttributes.find(x => x.applicationAccessProfileName == 'Super User') != null) {
      vm.admin = true;
      vm.superUser = true;
    } else if (vm.activeProject.applicationPolicyAttributes.find(x => x.applicationAccessProfileName == 'Admin') != null) {
      vm.admin = true;
      vm.superUser = false;
    } else {
      vm.admin = false;
      vm.superUser = false;
    }
  }

  close() {
    this.location.back();
  }

  loadRoleProfile() {
    const vm = this;
    vm.profileLoadingComplete = false;
    vm.userService.getUserProfile(vm.user.uuid)
      .subscribe(
        (result) => {
          vm.userProfile = result;
          vm.profileLoadingComplete = true;
        },
        (error) => vm.log.error('Error loading user profiles', error, 'Error')
      );
  }

  refresh(refresh : boolean = false) {
    const vm = this;
    vm.flushCache();
  }

  flushCache(){
    let vm = this;
    vm.configurationService.flushCache()
      .subscribe(
        (result) => {
          vm.loadRoleProfile();
        },
        (error) => vm.log.error('Flushing cache failed. Please try again.', error, 'Flush cache')
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
