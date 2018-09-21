import {Component, Input, OnInit} from '@angular/core';
import {User} from "../models/User";
import {UserProject} from "../models/UserProject";
import {Location} from "@angular/common";
import {ConfigurationService} from "../../configuration/configuration.service";
import {DelegationService} from "../../d3-delegation/delegation.service";
import {LoggerService, UserManagerService} from "eds-angular4";
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

  public activeRole: UserProject;
  superUser = false;
  godMode = false;

  constructor(private log: LoggerService,
              private $modal: NgbModal,
              private router: Router,
              private location: Location,
              protected userService: UserService,
              private delegationService: DelegationService,
              private configurationService: ConfigurationService,
              private state: ModuleStateService,
              private userManagerService: UserManagerService) { }

  ngOnInit() {
    let vm = this;

    let s = this.state.getState('userProfile');
    if (s == null) {
      this.user = {} as User;
      this.router.navigate(['user']);
      return;
    }
    this.user = Object.assign( {}, s.user);

    vm.userManagerService.activeRole.subscribe(active => {
      vm.activeRole = active;
      vm.roleChanged();
    });

    vm.refresh(false);
  }

  roleChanged() {
    const vm = this;
    if (vm.activeRole.applicationPolicyAttributes.find(x => x.applicationAccessProfileName == 'Admin') != null) {
      vm.superUser = true;
      vm.godMode = false;
    } else if (vm.activeRole.applicationPolicyAttributes.find(x => x.applicationAccessProfileName == 'God Mode') != null) {
      vm.superUser = true;
      vm.godMode = true;
    } else {
      vm.superUser = false;
      vm.godMode = false;
    }
  }

  close() {
    this.location.back();
  }

  loadRoleProfile() {
    const vm = this;

    vm.userService.getUserProfile(vm.user.uuid)
      .subscribe(
        (result) => {
          vm.userProfile = result;
          console.log(result);
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
