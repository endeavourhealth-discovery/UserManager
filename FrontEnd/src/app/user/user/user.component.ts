import {Component, OnInit, ViewChild} from '@angular/core';
import {LoggerService, MessageBoxDialog, SecurityService} from "eds-angular4";
import {UserService} from "../user.service";
import {NgbModal, NgbTabChangeEvent} from "@ng-bootstrap/ng-bootstrap";
import {User} from "../models/User";
import {UserEditorComponent} from "../user-editor/user-editor.component";
import {UserRole} from "../models/UserRole";
import {RoleType} from "../../configuration/models/RoleType";
import {ConfigurationService} from "../../configuration/configuration.service";
import {DelegationService} from "../../delegation/delegation.service";
import {Organisation} from "../../organisation/models/Organisation";

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit {

  userList: User[];
  roleTypes: RoleType[];
  selectedUser : User = null;
  selectedOrg: Organisation;
  filteredUserList : User[];
  delegatedOrganisations: Organisation[];
  sortReverse : boolean;
  sortField = 'username';
  searched : boolean;
  loadingRolesCompleted: boolean;
  hackOrganisation: string;
  hackDelegation: string;

  constructor(public log:LoggerService,
              private userService: UserService,
              private securityService: SecurityService,
              private configurationService: ConfigurationService,
              private delegationService: DelegationService,
              private $modal : NgbModal) {

  }

  ngOnInit() {
    this.getRoleTypes();
    this.getSelectedOrgs();
    this.getDelegatedOrganisations();
  }

  //gets all users in the realm
  getUsers(){
    let vm = this;
    vm.userList = null;
    vm.userService.getUsers(vm.selectedOrg.uuid)
      .subscribe(
        (result) => {
          vm.userList = result;
          vm.filteredUserList = result;
          console.log(result);
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

  getDelegatedOrganisations(){
    let vm = this;
    vm.delegationService.getDelegatedOrganisations(vm.delegationService.getSelectedOrganisation(), vm.delegationService.getSelectedDelegation())
      .subscribe(
        (result) => {
          vm.delegatedOrganisations = result;
          vm.selectedOrg = vm.delegatedOrganisations.find(r => {
            return r.uuid === vm.delegationService.getSelectedOrganisation();
          });
          vm.getUsers();

          console.log(result);
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
    let vm = this;
    UserEditorComponent.open(vm.$modal, null, false)
      .result.then(
      (editedUser) => vm.saveUser(null, editedUser),
      () => vm.log.info('User add cancelled')
    );
  }

  editUser(user:User) {
    let vm = this;
    UserEditorComponent.open(vm.$modal, user, true)
      .result.then(
      (editedUser) => vm.saveUser(user, editedUser),
      () => vm.log.info('User edit cancelled')
    );
  }

  private saveUser(user, editedUser : User) {
    let vm = this;
    let editMode = (user != null);

    vm.userService.saveUser(editedUser, editMode)
      .subscribe(
        (response) => {
          if (editMode) {
            vm.selectedUser = response;
            vm.updateUser(response);
          }

          let msg = (!editMode) ? 'Add user' : 'Edit user';
          vm.log.success('User saved', response, msg);
        },
        (error) => vm.log.error('Error saving user', error, 'Error')
      );
  }

  updateUser(editedUser: User){
    let vm = this;
    var index1 = 0;
    for(let user of vm.userList){
      if (user.uuid == editedUser.uuid){
        vm.userList[index1] = editedUser;
        if (vm.searched){
          var index2 = 0;
          for(let filteredUser of vm.filteredUserList){
            if (filteredUser.uuid == editedUser.uuid) {
              vm.filteredUserList[index2] = editedUser;
              return;
            }
            index2++;
          }
        }
        return;
      }
      index1++;
    }
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

  getSelectedOrgs() {
    const vm = this;
    vm.hackDelegation = vm.delegationService.getSelectedDelegation();
    vm.hackOrganisation = vm.delegationService.getSelectedOrganisation();
  }


}
