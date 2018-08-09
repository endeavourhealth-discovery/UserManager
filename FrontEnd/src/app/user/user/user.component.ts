import {Component, OnInit} from '@angular/core';
import {LoggerService, MessageBoxDialog, SecurityService, UserManagerService} from "eds-angular4";
import {UserService} from "../user.service";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {User} from "../models/User";
import {UserRole} from "../models/UserRole";
import {RoleType} from "../../configuration/models/RoleType";
import {ConfigurationService} from "../../configuration/configuration.service";
import {DelegationService} from "../../delegation/delegation.service";
import {DelegatedOrganisation} from "../../delegation/models/DelegatedOrganisation";
import {ActivatedRoute, Router} from "@angular/router";
import {ModuleStateService} from 'eds-angular4/dist/common';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit {
  private paramSubscriber: any;

  userList: User[];
  roleTypes: RoleType[];
  selectedUser : User = null;
  selectedOrg: DelegatedOrganisation;
  filteredUserList : User[];
  delegatedOrganisations: DelegatedOrganisation[];
  sortReverse : boolean;
  sortField = 'username';
  searched : boolean;
  loadingRolesCompleted: boolean;
  paramOrganisation: string;

  public activeRole: UserRole;
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
              private userManagerService: UserManagerService) {

  }

  ngOnInit() {
    this.getRoleTypes();

    this.userManagerService.activeRole.subscribe(active => {
      this.activeRole = active;
      this.roleChanged();
    });

    this.paramSubscriber = this.route.params.subscribe(
      params => {
        this.paramOrganisation = params['organisationId'];
      });
  }

  roleChanged() {
    const vm = this;
    if (vm.activeRole.roleTypeId == 'f0bc6f4a-8f18-11e8-839e-80fa5b320513') {
      vm.superUser = true;
    } else {
      vm.superUser = false;
    }
    vm.getDelegatedOrganisations();
  }

  //gets all users in the selected organisation
  getUsers(){
    let vm = this;
    vm.userList = null;
    vm.userService.getUsers(vm.selectedOrg.uuid)
      .subscribe(
        (result) => {
          vm.userList = result;
          vm.filteredUserList = result;
          vm.selectTopUser();
        },
        (error) => vm.log.error('Error loading users and roles', error, 'Error')
      );
  }

  getRoleTypes(){
    let vm = this;
    vm.configurationService.getRoleTypes()
      .subscribe(
        (result) => {
          vm.roleTypes = result;
        },
        (error) => vm.log.error('Error loading users and roles', error, 'Error')
      );
  }

  getDelegatedOrganisations() {
    let vm = this;
    let orgSelector = vm.paramOrganisation != null ? vm.paramOrganisation : vm.activeRole.organisationId;
    vm.delegationService.getDelegatedOrganisations(vm.activeRole.organisationId)
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

  sort(property: string) {
    const vm = this;
    vm.sortField = property;
    vm.sortReverse = !vm.sortReverse;

    vm.filteredUserList = vm.filteredUserList.sort(function(a, b) {
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
          vm.userService.deleteUser(userId)
            .subscribe(
              (result) => {
                result;
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
  }

  getUserRoles(userId: string){
    let vm = this;
    vm.loadingRolesCompleted = false;
    if (vm.selectedUser.userRoles) {
      vm.loadingRolesCompleted = true;
      return;
    }
    vm.userService.getUserRoles(userId)
      .subscribe(
        (result) => {
          vm.selectedUser.userRoles = vm.addRoleNameToRole(result);
          vm.loadingRolesCompleted = true;
        },
        (error) => vm.log.error('Error loading user roles', error, 'Error')
      );
  }

  addRoleNameToRole(userRoles : UserRole[]): UserRole[] {
    const vm = this;
    for (let role of userRoles) {
      var result = vm.roleTypes.find(r => {
        return r.id === role.roleTypeId;
      });

      role.roleTypeName = result.name;
    }
    return userRoles;
  }
}
