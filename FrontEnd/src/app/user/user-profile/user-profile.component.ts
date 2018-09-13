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

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.css']
})
export class UserProfileComponent implements OnInit {

  @Input() user: User;
  userProfiles: UserProfile[];

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
    if (vm.activeRole.projectId == 'f0bc6f4a-8f18-11e8-839e-80fa5b320513') {
      vm.superUser = true;
      vm.godMode = false;
    } else if (vm.activeRole.projectId == '3517dd59-9ecb-11e8-9245-80fa5b320513') {
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
          vm.userProfiles = result;
          console.log(result);
        },
        (error) => vm.log.error('Error loading user profiles', error, 'Error')
      );
  }

  refresh(refresh : boolean = false) {
    const vm = this;
    vm.loadRoleProfile();
  }

  getStringListFromArray(dataArray: any) {

    var stringArray = []

    for (let idx in dataArray) {
      stringArray.push(dataArray[idx]);
    }


    return stringArray.toString();

  }

}
