import {Component, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {User} from "../../models/User";
import {UserService} from "../user.service";
import {GenericTableComponent, LoggerService, MessageBoxDialogComponent, UserManagerService} from "dds-angular8";
import {DelegatedOrganisation} from "../../d3-delegation/models/DelegatedOrganisation";
import {DelegationService} from "../../d3-delegation/delegation.service";
import {MatDialog} from '@angular/material';
import {UserDialogComponent} from "../user-dialog/user-dialog.component";
import {UserPickerComponent} from "../user-picker/user-picker.component";
import {UserProject} from "dds-angular8/lib/user-manager/models/UserProject";
import { UserProject_local } from 'src/app/models/UserProject_local';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit {

  @ViewChild('userListTable', {static: false}) userListTable: GenericTableComponent;

  private paramSubscriber: any;

  userList: User[] = [];
  selectedUser : User = null;
  selectedOrg: DelegatedOrganisation;
  delegatedOrganisations: DelegatedOrganisation[];
  loadingRolesCompleted: boolean;
  paramOrganisation: string;
  selectedUserCreatedDate: string;
  machineUsers = false;

  userDetailsToShow = new User().getDisplayItems();
  userProjectDetailsToShow = new User().getUserProjectDisplayItems();

  public activeProject: UserProject;
  admin = false;
  superUser = false;

  constructor(public log: LoggerService,
              private userService: UserService,
              private delegationService: DelegationService,
              private router: Router,
              private route: ActivatedRoute,
              private userManagerService: UserManagerService,
              public dialog: MatDialog
  ) {

  }

  ngOnInit() {

    this.paramSubscriber = this.route.params.subscribe(
      params => {
        this.paramOrganisation = params['organisationId'];
      });

    this.userManagerService.onProjectChange.subscribe(active => {
      this.activeProject = active;
      this.roleChanged();
    });
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

    if (this.superUser) {
      this.getGodModeOrganisations();
    } else {
      this.getDelegatedOrganisations();
    }
  }

  //gets all users in the selected organisation
  getUsers(){
    this.userService.getUsers(this.selectedOrg.uuid, null, this.machineUsers)
      .subscribe(
        (result) => {
          this.userList = result;
          this.selectTopUser();
        },
        (error) => this.log.error('Error loading users and roles')
      );
  }

  getDelegatedOrganisations() {
    let orgSelector = this.paramOrganisation != null ? this.paramOrganisation : this.activeProject.organisationId;
    this.delegationService.getDelegatedOrganisations(this.activeProject.organisationId)
      .subscribe(
        (result) => {
          this.delegatedOrganisations = result;
          this.selectedOrg = this.delegatedOrganisations.find(r => {
            return r.uuid === orgSelector;
          });
          this.getUsers();
        },
        (error) => this.log.error('Error loading delegated organisations')
      );
  }

  getGodModeOrganisations() {
    let orgSelector = this.paramOrganisation != null ? this.paramOrganisation : this.activeProject.organisationId;
    this.delegationService.getGodModeOrganisations()
      .subscribe(
        (result) => {
          this.delegatedOrganisations = result;
          this.selectedOrg = this.delegatedOrganisations.find(r => {
            return r.uuid === orgSelector;
          });
          if (this.selectedOrg == null) {
            this.selectedOrg = this.delegatedOrganisations[0];
          }
          this.getUsers();
        },
        (error) => this.log.error('Error loading delegated organisations.')
      );
  }

  selectTopUser() {
    if (this.userList != null && this.userList.length > 0)
    {
      let topUserInList = this.userList[0];
      this.selectedUser = topUserInList;
      this.getUserRoles(this.selectedUser.uuid);
      this.convertDateForSelectedUser();
    }
    else {
      this.selectedUser = null;
    }
  }

  addUser() {
    this.delegationService.updateSelectedOrganisation(this.selectedOrg.uuid);
    // this.state.setState('userEdit', {user: null, editMode: false});
    //this.router.navigate(['userEdit'], {state: {user: null, editMode: false}});
    const dialogRef = this.dialog.open(UserDialogComponent, {
      data: {editMode: false, user: null},
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.router.navigate(['userEdit'], {state: {user: result, editMode: true}});
        this.log.success('User saved');
      }
    });
  }

  addExisting() {
    this.delegationService.updateSelectedOrganisation(this.selectedOrg.uuid);
    // this.state.setState('userEdit', {user: null, editMode: true, existing: true});
    // this.router.navigate(['userEdit'], {state: {user: null, editMode: true, existing: true}});
    const dialogRef = this.dialog.open(UserPickerComponent, {
      minWidth: '50vw',
      data: {uuid: '', limit: 0, userId : null, existing: this.userList},
    })
    dialogRef.afterClosed().subscribe(result => {
      if (!result) {
        return;
      }
      for (let user of result) {
        var project = new UserProject_local();
        project.userId = user.uuid;
        project.organisationId = this.selectedOrg.uuid;
        project.organisationName = this.selectedOrg.name;
        project.projectId = this.activeProject.projectId;
        project.projectName = this.activeProject.projectName;
        project.deleted = false;
        project.default = false;
        if (user.userProjects) {
          user.userProjects.push(project);
        } else {
          user.userProjects = [];
          user.userProjects.push(project);
        }
      }
      this.userService.saveUsers(result, this.activeProject.id).subscribe(result => {
        if (!result) {
          return;
        }
        for (let user of result) {
          if (!this.userList.some(x => x.uuid === user.uuid)) {
            this.userList.push(user);
            this.userListTable.updateRows();
          }
          this.log.success('Users added successfully.');
        }
      })
    },(error) => this.log.error('Failed to add users.'));
  }

  editUser(user:User) {
    this.delegationService.updateSelectedOrganisation(this.selectedOrg.uuid);
    // this.state.setState('userEdit', {user: user, editMode: true});
    this.router.navigate(['userEdit'], {state: {user: user, editMode: true}});
  }

  viewProfile(user: User) {
    this.delegationService.updateSelectedOrganisation(this.selectedOrg.uuid);
    // this.state.setState('userProfile', {user: user});
    this.router.navigate(['userProfile'], {state: {user: user}});
  }

  resendEmail(user: User) {

    this.userService.sendUserPasswordEmail(user.uuid, this.activeProject.id)
      .subscribe(
        (result) => {
          this.log.success('Reset password email sent successfully.');
          this.loadingRolesCompleted = true;
        },
        (error) => this.log.error('Error sending reset password email.')
      );
  }

  deleteUser(user:User) {

    let loggedOnUserUuid = this.activeProject.userId;
    if (user.uuid == loggedOnUserUuid)
    {
      this.log.error("You cannot delete yourself!");
    }
    else {
      let userName = user.forename + " " + user.surname;
      MessageBoxDialogComponent.open(this.dialog, 'Delete user', 'Are you sure you want to delete user: ' + userName.trim() + '?',
        'Delete user', 'No')
        .subscribe(
          (result) => {
            if (result) {
              let userId = user.uuid;
              this.userService.deleteUser(userId, this.activeProject.id)
                .subscribe(
                  (result) => {
                    this.getUsers();
                    this.selectedUser = null;
                    this.log.info("User deleted");
                  },
                  (error) => this.log.error('Error deleting user.')
                );
            }
          });
    }
  }

  changeSelectedUser(user: User) {

    this.selectedUser = user;
    this.getUserRoles(user.uuid)
    this.convertDateForSelectedUser();
  }

  convertDateForSelectedUser() {

    var d = new Date(this.selectedUser.createdTimeStamp);
    this.selectedUserCreatedDate = d.toLocaleString();

  }

  getUserRoles(userId: string){

    this.loadingRolesCompleted = false;
    if (this.selectedUser.userProjects) {
      this.loadingRolesCompleted = true;
      return;
    }
    this.userService.loadUserProjects(userId)
      .subscribe(
        (result) => {
          this.selectedUser.userProjects = result;
          this.loadingRolesCompleted = true;
        },
        (error) => this.log.error('Error loading user projects.')
      );
  }

  viewMachineUsers() {
    this.machineUsers = true;
    this.getUsers();
  }

  viewHumanUsers() {
    this.machineUsers = false;
    this.getUsers();
  }
}
