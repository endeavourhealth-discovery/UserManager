import { Component, OnInit } from '@angular/core';
import {LoggerService, UserManagerService} from "eds-angular4";
import {UserProject} from "../models/UserProject";
import {User} from "../models/User";
import {UserService} from "../user.service";
import {ConfigurationService} from "../../configuration/configuration.service";
import {ApplicationPolicy} from "../../configuration/models/ApplicationPolicy";
import {Router} from "@angular/router";
import {ModuleStateService} from "eds-angular4/dist/common";

@Component({
  selector: 'app-user-view',
  templateUrl: './user-view.component.html',
  styleUrls: ['./user-view.component.css']
})
export class UserViewComponent implements OnInit {
  loadingRolesCompleted = false;
  selectedUser : User = null;
  roleTypes: ApplicationPolicy[];

  public activeRole: UserProject;
  superUser = false;

  constructor(private log: LoggerService,
              private userManagerService: UserManagerService,
              private userService: UserService,
              private configurationService: ConfigurationService,
              private router: Router,
              private state: ModuleStateService) { }

  ngOnInit() {
    const vm = this;
    vm.getRoleTypes();
    vm.userManagerService.activeRole.subscribe(active => {
      vm.activeRole = active;
      vm.roleChanged();
    });
  }

  roleChanged() {
    const vm = this;
    if (vm.activeRole.projectId == 'f0bc6f4a-8f18-11e8-839e-80fa5b320513') {
      vm.superUser = true;
    } else if (vm.activeRole.projectId == '3517dd59-9ecb-11e8-9245-80fa5b320513') {
      vm.superUser = true;
    } else {
      vm.superUser = false;
    }
    vm.loadUser();
  }

  loadUser()
  {
    var vm = this;
    vm.selectedUser = null;
    let userId = vm.activeRole.userId;

    vm.userService.getUser(userId)
      .subscribe(
        (result) => {
          vm.selectedUser = result;
          vm.getUserRoles(vm.selectedUser.uuid);
        },
        (error) => vm.log.error('Error loading user', error, 'Error')
      );
  }

  editUser(selectedUser: User) {
    this.state.setState('userEdit', {user: selectedUser, editMode: true, selfEdit: true});
    this.router.navigate(['userEdit']);
  }

  getUserRoles(userId: string){
    let vm = this;
    vm.loadingRolesCompleted = false;
    if (vm.selectedUser.userProjects) {
      vm.loadingRolesCompleted = true;
      return;
    }
    vm.userService.getUserRoles(userId)
      .subscribe(
        (result) => {
          vm.selectedUser.userProjects = vm.addRoleNameToRole(result);
          vm.loadingRolesCompleted = true;
        },
        (error) => vm.log.error('Error loading user roles', error, 'Error')
      );
  }

  addRoleNameToRole(userRoles : UserProject[]): UserProject[] {
    const vm = this;
    for (let role of userRoles) {
      var result = vm.roleTypes.find(r => {
        return r.id === role.projectId;
      });

      role.projectName = result.name;
    }
    return userRoles;
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

}
