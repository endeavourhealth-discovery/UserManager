import {Component, Input, OnInit} from '@angular/core';
import {User} from "../models/User";
import {UserProject} from "../models/UserProject";
import {ModuleStateService} from "eds-angular4/dist/common";
import {LoggerService, MessageBoxDialog, UserManagerNotificationService, UserManagerService} from "eds-angular4";
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

    let s = this.state.getState('userBio');
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
    vm.selectedSharingAgreement = null;
    vm.selectedApp = null;
    vm.selectedProfileTree = null;
    vm.selectedProfile = null;

    vm.userService.getRoleAccessProfile(vm.selectedRole.projectId, vm.selectedRole.organisationId)
      .subscribe(
        (result) => {
          vm.accessProfiles = result;
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
    vm.selectedSharingAgreement = null;
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
