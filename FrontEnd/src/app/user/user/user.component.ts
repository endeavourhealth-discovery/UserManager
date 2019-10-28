import {Component, OnInit} from '@angular/core';
import {
  LoggerService,
  MessageBoxDialog,
  SecurityService,
  UserManagerNotificationService,
  UserManagerService
} from "eds-angular4";
import {UserService} from "../user.service";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {User} from "../models/User";
import {ConfigurationService} from "../../configuration/configuration.service";
import {DelegationService} from "../../d3-delegation/delegation.service";
import {DelegatedOrganisation} from "../../d3-delegation/models/DelegatedOrganisation";
import {ActivatedRoute, Router} from "@angular/router";
import {ModuleStateService} from 'eds-angular4/dist/common';
import {UserProject} from "eds-angular4/dist/user-manager/models/UserProject";

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit {
  private paramSubscriber: any;

  userList: User[];
  selectedUser : User = null;
  selectedOrg: DelegatedOrganisation;
  filteredUserList : User[];
  delegatedOrganisations: DelegatedOrganisation[];
  sortReverse : boolean;
  sortField = 'username';
  searched : boolean;
  loadingRolesCompleted: boolean;
  paramOrganisation: string;
  searchTerm: string;
  selectedUserCreatedDate: string;
  machineUsers = false;

  public activeProject: UserProject;
  admin = false;
  superUser = false;

  constructor(public log:LoggerService,
              private userService: UserService,
              private securityService: SecurityService,
              private configurationService: ConfigurationService,
              private delegationService: DelegationService,
              private $modal : NgbModal,
              private router: Router,
              private route: ActivatedRoute,
              private state: ModuleStateService,
              private userManagerNotificationService: UserManagerNotificationService,
              private userManagerService: UserManagerService,) {

  }

  ngOnInit() {

    this.paramSubscriber = this.route.params.subscribe(
      params => {
        this.paramOrganisation = params['organisationId'];
      });

    this.userManagerNotificationService.activeUserProject.subscribe(active => {
      this.activeProject = active;
      this.roleChanged();
    });
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

    if (vm.superUser) {
      vm.getGodModeOrganisations();
    } else {
      vm.getDelegatedOrganisations();
    }
  }

  //gets all users in the selected organisation
  getUsers(){
    let vm = this;
    vm.userList = null;
    vm.filteredUserList = null;
    vm.userService.getUsers(vm.selectedOrg.uuid, null, vm.machineUsers)
      .subscribe(
        (result) => {
          vm.userList = result;
          vm.filteredUserList = result;
          vm.selectTopUser();
        },
        (error) => vm.log.error('Error loading users and roles', error, 'Error')
      );
  }

  searchUsers() {
    const vm = this;
    vm.filteredUserList = vm.userList;
    vm.filteredUserList = vm.filteredUserList.filter(
      user => user.username.includes(vm.searchTerm) || user.forename.includes(vm.searchTerm) || user.surname.includes(vm.searchTerm)
    );
  }

  clearSearch() {
    const vm = this;
    vm.searchTerm = '';
    vm.filteredUserList = vm.userList;
  }

  getDelegatedOrganisations() {
    let vm = this;
    let orgSelector = vm.paramOrganisation != null ? vm.paramOrganisation : vm.activeProject.organisationId;
    vm.delegationService.getDelegatedOrganisations(vm.activeProject.organisationId)
      .subscribe(
        (result) => {
          vm.delegatedOrganisations = result;
          vm.selectedOrg = vm.delegatedOrganisations.find(r => {
            return r.uuid === orgSelector;
          });
          vm.getUsers();
        },
        (error) => vm.log.error('Error loading delegated organisations', error, 'Error')
      );
  }

  getGodModeOrganisations() {
    let vm = this;
    let orgSelector = vm.paramOrganisation != null ? vm.paramOrganisation : vm.activeProject.organisationId;
    vm.delegationService.getGodModeOrganisations()
      .subscribe(
        (result) => {
          vm.delegatedOrganisations = result;
          vm.selectedOrg = vm.delegatedOrganisations.find(r => {
            return r.uuid === orgSelector;
          });
          if (vm.selectedOrg == null) {
            vm.selectedOrg = vm.delegatedOrganisations[0];
          }
          vm.getUsers();
        },
        (error) => vm.log.error('Error loading delegated organisations', error, 'Error')
      );
  }

  sort(property: string) {
    const vm = this;
    vm.sortField = property;
    vm.sortReverse = !vm.sortReverse;

    vm.filteredUserList.sort(function(a, b) {
      var result = (a[property] < b[property]) ? -1 : (a[property] > b[property]) ? 1 : 0;
      if (vm.sortReverse) {
        return result * -1;
      } else {
        return result;
      }
    })
  }

  selectTopUser(){
    let vm = this;
    if (vm.filteredUserList != null && vm.filteredUserList.length > 0)
    {
      let topUserInList = vm.filteredUserList[0];
      vm.selectedUser = topUserInList;
      vm.getUserRoles(vm.selectedUser.uuid);
      vm.convertDateForSelectedUser();
    }
    else {
      vm.selectedUser = null;
    }
  }

  hasPermission(role : string) : boolean {
    return this.securityService.hasPermission('eds-user-manager', role);
  }

  addUser() {
    this.delegationService.updateSelectedOrganisation(this.selectedOrg.uuid);
    this.state.setState('userEdit', {user: null, editMode: false});
    this.router.navigate(['userEdit']);
  }

  addExisting() {
    this.delegationService.updateSelectedOrganisation(this.selectedOrg.uuid);
    this.state.setState('userEdit', {user: null, editMode: true, existing: true});
    this.router.navigate(['userEdit']);
  }

  editUser(user:User) {
    this.delegationService.updateSelectedOrganisation(this.selectedOrg.uuid);
    this.state.setState('userEdit', {user: user, editMode: true});
    this.router.navigate(['userEdit']);
  }

  viewBio(user: User) {
    this.delegationService.updateSelectedOrganisation(this.selectedOrg.uuid);
    this.state.setState('userBio', {user: user});
    this.router.navigate(['userBio']);
  }

  viewProfile(user: User) {
    this.delegationService.updateSelectedOrganisation(this.selectedOrg.uuid);
    this.state.setState('userProfile', {user: user});
    this.router.navigate(['userProfile']);
  }

  resendEmail(user: User) {
    const vm = this;
    vm.userService.sendUserPasswordEmail(user.uuid)
      .subscribe(
        (result) => {
          vm.log.success('Reset password email sent successfully', null, 'Sending email');
          vm.loadingRolesCompleted = true;
        },
        (error) => vm.log.error('Error sending reset password email', error, 'Sending email')
      );
  }

  deleteUser(user:User) {
    let vm = this;
    let loggedOnUserUuid = this.securityService.getCurrentUser().uuid;
    if (user.uuid == loggedOnUserUuid)
    {
      vm.log.warning("You cannot delete yourself!");
    }
    else {
      let userName = user.forename + " " + user.surname;

      MessageBoxDialog.open(vm.$modal, "Confirmation", "Delete user: " + userName.trim() + "?", "Yes", "No")
        .result.then(
        (result) => {
          let userId = user.uuid;
          vm.userService.deleteUser(userId, vm.activeProject.id)
            .subscribe(
              (result) => {
                vm.getUsers();
                vm.selectedUser = null;
                vm.log.info("User deleted");
              },
              (error) => vm.log.error('Error deleting user', error, 'Error')
            );
        },
        (reason) => {
        }
      );
    }
  }

  changeSelectedUser(user: User) {
    const vm = this;
    vm.selectedUser = user;
    vm.getUserRoles(user.uuid)
    vm.convertDateForSelectedUser();
  }

  convertDateForSelectedUser() {
    const vm = this;
    var d = new Date(vm.selectedUser.createdTimeStamp);
    vm.selectedUserCreatedDate = d.toLocaleString();

  }

  getUserRoles(userId: string){
    let vm = this;
    vm.loadingRolesCompleted = false;
    if (vm.selectedUser.userProjects) {
      vm.loadingRolesCompleted = true;
      return;
    }
    vm.userManagerService.getUserProjects(userId)
      .subscribe(
        (result) => {
          vm.selectedUser.userProjects = result;
          vm.loadingRolesCompleted = true;
        },
        (error) => vm.log.error('Error loading user projects', error, 'Error')
      );
  }

  viewMachineUsers() {
    const vm = this;
    vm.machineUsers = true;
    vm.getUsers();
  }

  viewHumanUsers() {
    const vm = this;
    vm.machineUsers = false;
    vm.getUsers();
  }
}
