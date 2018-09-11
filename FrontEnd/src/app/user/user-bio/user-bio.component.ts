import {Component, Input, OnInit} from '@angular/core';
import {User} from "../models/User";
import {UserProject} from "../models/UserProject";
import {ModuleStateService} from "eds-angular4/dist/common";
import {LoggerService, MessageBoxDialog, UserManagerService} from "eds-angular4";
import {Router} from "@angular/router";
import {DelegationService} from "../../d3-delegation/delegation.service";
import {UserService} from "../user.service";
import {Location} from "@angular/common";
import {ConfigurationService} from "../../configuration/configuration.service";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {UserAccessProfile} from "../models/UserAccessProfile";
import {ApplicationPolicyAttribute} from "../../configuration/models/ApplicationPolicyAttribute";

@Component({
  selector: 'app-user-bio',
  templateUrl: './user-bio.component.html',
  styleUrls: ['./user-bio.component.css']
})
export class UserBioComponent implements OnInit {

  @Input() user: User;
  selectedRole: UserProject;
  accessProfiles: UserAccessProfile[];
  selectedApp: UserAccessProfile;
  selectedProfile: ApplicationPolicyAttribute;
  selectedProfileTree: any;
  selectedSharingAgreement: any;

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

    let s = this.state.getState('userBio');
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

  getRoles(refresh: boolean) {
    const vm = this;
    if (!vm.user.userProjects || refresh) {
      vm.userService.getUserRoles(vm.user.uuid)
        .subscribe(
          (result) => {
            vm.user.userProjects = result;
            if (vm.user.userProjects && vm.user.userProjects.length > 0) {
              vm.selectedRole = vm.user.userProjects[0];
            }
          },
          (error) => vm.log.error('Error loading user roles', error, 'Error')
        );
    } else {
      if (vm.user.userProjects && vm.user.userProjects.length > 0) {
        vm.selectedRole = vm.user.userProjects[0];
      }
    }
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
    vm.selectedSharingAgreement = null;
    vm.selectedApp = null;
    vm.selectedProfileTree = null;
    vm.selectedProfile = null;

    vm.userService.getRoleAccessProfile(vm.selectedRole.projectId, vm.selectedRole.organisationId)
      .subscribe(
        (result) => {
          vm.accessProfiles = result;
          console.log(result);
        },
        (error) => vm.log.error('Error loading access profiles', error, 'Error')
      );
  }

  selectApp(app: UserAccessProfile) {
    const vm = this;
    vm.selectedApp = app;
    vm.selectedSharingAgreement = null;
    vm.selectedProfileTree = null;
    vm.selectedProfile = null;
  }

  setButtonClass(canAccessData: boolean) {
    if (canAccessData) {
      return 'btn btn-danger';
    } else {
      return 'btn btn-info';
    }
  }

  selectProfile(profile: ApplicationPolicyAttribute) {
    const vm = this;
    vm.selectedProfile = profile;
    vm.selectedProfileTree = JSON.parse(profile.profileTree);
    vm.selectedSharingAgreement = null;
    console.log(vm.selectedProfileTree);
  }

  selectAgreement(agreement: any) {
    const vm = this;
    vm.selectedSharingAgreement = agreement;
  }

  viewSharingAgreement(agreement: any) {
    var url = window.location.protocol + "//" + window.location.host;
    url = url + "/data-sharing-manager/#/dsa/" + agreement.sharingAgreementId + '/edit';
    window.open(url, '_blank');
  }

  refresh(refresh : boolean = false) {
    const vm = this;
    vm.getRoles(refresh);
    vm.loadRoleProfile();
  }
}
