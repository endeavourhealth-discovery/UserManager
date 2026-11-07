import {Component, Inject, OnInit, ViewChild} from '@angular/core';
import {User} from "../../models/User";
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from "@angular/material/dialog";
import {
  GenericTableComponent,
  ItemLinkageService,
  LoggerService,
  MessageBoxDialogComponent,
  UserManagerService
} from "dds-angular8";
import {DatePipe} from "@angular/common";
import {UserService} from "../user.service";
import {UserProject} from "dds-angular8/user-manager/models/UserProject";
import {Region} from "../../models/Region";
import {ApplicationPolicy} from "../../models/ApplicationPolicy";
import {UserRegion} from "../../models/UserRegion";
import {UserApplicationPolicy} from "../../models/UserApplicationPolicy";
import {ConfigurationService} from "../../configuration/configuration.service";
import {ProjectPickerComponent} from "../project-picker/project-picker.component";
import {DelegatedOrganisation} from "../../d3-delegation/models/DelegatedOrganisation";
import {DelegationService} from "../../d3-delegation/delegation.service";
import {OrganisationService} from "../../organisation/organisation.service";
import {Project} from "../../models/Project";

export interface DialogData {
  user: User;
  editMode: boolean;
  availableRegions: Region[];
  availablePolicies: ApplicationPolicy[];
}

@Component({
  selector: 'app-user-dialog',
  templateUrl: './new-user-dialog.component.html',
  styleUrls: ['./new-user-dialog.component.scss']
})
export class NewUserDialogComponent implements OnInit {

  resultData: User;
  editMode: boolean;
  activeProject: UserProject;
  admin = false;
  superUser = false;

  userRegion: UserRegion;
  availableRegions: Region[];
  selectedRegion: Region;

  userApplicationPolicy: UserApplicationPolicy;
  availablePolicies: ApplicationPolicy[];
  selectedApplicationPolicy: ApplicationPolicy;
  userProjectsFromDB: UserProject[] = [];
  delegatedOrganisations: DelegatedOrganisation[];
  selectedOrg: DelegatedOrganisation;
  organisationProjects: Project[];
  userProjectDetailsToShow = new User().getUserProjectDisplayItems();

  @ViewChild('username', {static: false}) usernameBox;
  @ViewChild('forename', {static: false}) forenameBox;
  @ViewChild('surname', {static: false}) surnameBox;
  @ViewChild('email', {static: false}) emailBox;
  @ViewChild('photo', {static: false}) photoURLBox;
  @ViewChild('password1', {static: false}) password1Box;
  @ViewChild('password2', {static: false}) password2Box;
  @ViewChild('userProjects', {static: false}) userProjects: GenericTableComponent;

  constructor(public dialogRef: MatDialogRef<NewUserDialogComponent>,
              private log: LoggerService,
              @Inject(MAT_DIALOG_DATA) public data: DialogData,
              protected userService: UserService,
              private datePipe: DatePipe,
              private configurationService: ConfigurationService,
              private userManagerService: UserManagerService,
              public dialog: MatDialog,
              private linkageService: ItemLinkageService,
              private delegationService: DelegationService,
              private organisationService: OrganisationService) {
    this.resultData = data.user;
    if (!this.resultData) {
      this.resultData = new User();
    }
    this.editMode = data.editMode;
    this.availableRegions = data.availableRegions;
    this.availablePolicies = data.availablePolicies;
  }

  ngOnInit() {
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
    this.getAvailableRegions();
    this.getAvailableApplicationPolicies();
    if (this.superUser) {
      this.getGodModeOrganisations();
    } else {
      this.getDelegatedOrganisations();
    }
  }

  changeUserRegion(regionUuid: string) {
    let changedRegion = new UserRegion();
    changedRegion.userId = this.resultData.uuid;
    changedRegion.regionId = regionUuid;
    this.userRegion = changedRegion;
  }

  changeUserApplicationPolicy(policyId: string) {
    let changedPolicy = new UserApplicationPolicy();
    changedPolicy.userId = this.resultData.uuid;
    changedPolicy.applicationPolicyId = policyId;
    this.userApplicationPolicy = changedPolicy;
  }

  validateFormInput() {
    //go down each tab. check content and flip to and highlight if not complete
    var vm = this;
    var result = true;
    if (this.resultData.userProjects.length == 0) {
      this.log.error('user must be added to at least 1 project');
      result = false;
    }
    //username is mandatory
    if (this.resultData.username.trim() == '') {
      this.log.error('Username must not be blank');
      this.usernameBox.nativeElement.focus();
      result = false;
    } else
      //forename is mandatory
    if (this.resultData.forename.trim() == '') {
      this.log.error('Forename must not be blank');
      this.forenameBox.nativeElement.focus();
      result = false;
    } else
      //surname is mandatory
    if (this.resultData.surname.trim() == '') {
      this.log.error('Surname must not be blank');
      this.surnameBox.nativeElement.focus();
      result = false;
    } else
      //email is mandatory
    if (this.resultData.email.trim() == '') {
      this.log.error('Email address must not be blank');
      this.emailBox.nativeElement.focus();
      result = false;
    } else if (!this.selectedRegion && this.editMode) {
      this.log.error('User region must be selected');
      result = false;
    } else if (!this.selectedApplicationPolicy && this.editMode) {
      this.log.error('User application policy must be selected');
      result = false;
    } else if (this.resultData.photo != null && this.resultData.photo.length > 100) {
      this.log.error('Length of image URL is too long. Consider using bitly or similar to shorten it');
      this.photoURLBox.nativeElement.focus();
      result = false;
    } else
      //check changed passwords match and are valid for a new user addition
    {
      let passwordInput = this.resultData.password.trim();
      if (passwordInput == '') {
        /*if (!this.isEditMode()) {
          this.log.warning('Password must not be blank');
          this.password1Box.nativeElement.focus();
          result = false;
        }*/
      } else
        //passwords must match and map onto the password policy (1 upper, 1 digit, 8 length and not be the same as username)
      if (this.password2Box.nativeElement.value != this.password1Box.nativeElement.value) {
        this.log.error('Passwords must match');
        this.password2Box.nativeElement.focus();
        result = false;
      } else if (passwordInput.length < 8) {
        this.log.error('Password must be at least 8 characters long');
        this.password1Box.nativeElement.focus();
        result = false;
      } else if (!/\d/.test(passwordInput)) {
        this.log.error('Password must contain at least 1 number');
        this.password1Box.nativeElement.focus();
        result = false;
      } else if (!/[A-Z]/.test(passwordInput)) {
        this.log.error('Password must contain at least 1 Uppercase letter');
        this.password1Box.nativeElement.focus();
        result = false;
      } else if (passwordInput == this.resultData.username.trim()) {
        this.log.error('Password cannot be the same as username');
        this.password1Box.nativeElement.focus();
        result = false;
      }
    }
    return result;
  }

  saveRegion() {
    if (this.userRegion.userId == null) {
      this.userRegion.userId = this.resultData.uuid;
    }
    this.userService.saveUserRegion(this.userRegion, this.activeProject.id)
      .subscribe(
        (response) => {
        },
        (error) => this.log.error('User region could not be saved. Please try again.')
      );
  }

  saveApplicationPolicy() {
    if (this.userApplicationPolicy.userId == null) {
      this.userApplicationPolicy.userId = this.resultData.uuid;
    }
    this.userService.saveUserApplicationPolicy(this.userApplicationPolicy, this.activeProject.id)
      .subscribe(
        (response) => {
        },
        (error) => this.log.error('User application policy could not be saved. Please try again.')
      );
  }

  getUserRegion() {
    this.userService.getUserRegion(this.resultData.uuid)
      .subscribe(
        (result) => {
          this.userRegion = result;
          this.selectedRegion = this.availableRegions.find(r => {
            return r.uuid === this.userRegion.regionId;
          });
        },
        (error) => {
          this.log.error('User region could not be loaded. Please try again.\n' + error);
        }
      );
  }

  getAvailableApplicationPolicies() {
    const vm = this;
    vm.configurationService.getApplicationPolicies()
      .subscribe(
        (result) => {
          vm.availablePolicies = result;
        },
        (error) => {
          vm.log.error('Available application policies could not be loaded. Please try again.\n' + error);
        }
      );
  }

  getDelegatedOrganisations() {

    this.delegationService.getDelegatedOrganisations(this.activeProject.organisationId)
      .subscribe(
        (result) => {
          this.delegatedOrganisations = result;
          this.selectedOrg = this.delegatedOrganisations.find(r => {
            return r.uuid === this.delegationService.getSelectedOrganisation();
          });
          this.getOrganisationProjects(this.selectedOrg.uuid);

        },
        (error) => this.log.error('Error loading delegated organisations')
      );
  }

  getGodModeOrganisations() {

    this.delegationService.getGodModeOrganisations()
      .subscribe(
        (result) => {
          this.delegatedOrganisations = result;
          this.selectedOrg = this.delegatedOrganisations.find(r => {
            return r.uuid === this.delegationService.getSelectedOrganisation();
          });
          // this.getOrganisationProjects(this.selectedOrg.uuid);

        },
        (error) => this.log.error('Error loading delegated organisations')
      );
  }

  addProject() {
    if (this.validateFormInput() == true) {
      this.saveCurrentUser();
      const dialogRef = this.dialog.open(ProjectPickerComponent, {
        minWidth: '50vw',
        data: {delegatedOrganisations: this.delegatedOrganisations, userId: this.resultData.uuid},
      });
      dialogRef.afterClosed().subscribe(result => {
        if (!result) {
          return;
        }
        for (let userProjects of result) {
          if (this.resultData.userProjects) {
            this.resultData.userProjects.push(userProjects);
          } else {
            this.resultData.userProjects = [];
            this.resultData.userProjects.push(userProjects);
          }
        }
        this.userService.saveUserProjects(this.resultData.userProjects, this.activeProject.id).subscribe(
          result => {
            this.getUserProjects(this.resultData.uuid);
            this.log.success('User saved');
          },
          (error) => this.log.error('User details could not be saved. Please try again.')
        )
      });
    }
  }

  getUserProjects(userId: string) {
    this.userService.loadUserProjects(userId)
      .subscribe(
        (result) => {
          this.userProjectsFromDB = result;
        },
        (error) => this.log.error('Error loading user projects' + error + 'Error')
      );
  }

  getUserApplicationPolicy() {
    this.userService.getUserApplicationPolicy(this.resultData.uuid)
      .subscribe(
        (result) => {
          this.userApplicationPolicy = result;
          this.selectedApplicationPolicy = this.availablePolicies.find(r => {
            return r.id === this.userApplicationPolicy.applicationPolicyId;
          });
        },
        (error) => {
          //this.log.error('User application policy could not be loaded. Please try again.');
        }
      );
  }

  getOrganisationProjects(organisationId: string) {
    let vm = this;
    vm.organisationService.getProjectsForOrganisation(organisationId)
      .subscribe(
        (result) => {
          vm.organisationProjects = result;
          vm.checkAvailableProjects()
        },
        (error) => vm.log.error('Error loading organisation projects \n' + error)
      );
  }


  ok() {
    if (this.validateFormInput() == true) {
      this.saveCurrentUser();
      this.dialogRef.close(this.resultData);
    }
  }

  cancel() {
    this.dialogRef.close();
  }

  private saveCurrentUser() {
    this.userService.saveUser(this.resultData, this.editMode, this.activeProject.id)
      .subscribe(
        (response) => {
          response.password = '';  // blank out password on save
          this.resultData = response;
          this.saveRegion();
          this.saveApplicationPolicy();
        },
        (error) => this.log.error('User details could not be saved. Please try again.')
      );
  }

  private getAvailableRegions() {
    this.userService.getAvailableRegions()
      .subscribe(
        (result) => {
          this.availableRegions = result;
        },
        (error) => {
          this.log.error('Available regions could not be loaded. Please try again.');
        }
      );
  }

  checkAvailableProjects() {
    const vm = this;
    if (vm.resultData.userProjects) {
      for (let role of vm.resultData.userProjects) {
        if (!role.deleted && role.organisationId === vm.selectedOrg.uuid) {
          var roleToDelete = vm.organisationProjects.find(e => e.uuid === role.projectId);
          if (roleToDelete != null) {
            let i = vm.organisationProjects.indexOf(roleToDelete);
            if (i !== -1) {
              vm.organisationProjects.splice(i, 1);
            }
          }
        }
      }
    }
  }

  removeCurrentRole() {
    MessageBoxDialogComponent.open(this.dialog, 'Remove user projects', 'Are you sure you want to remove user projects?',
      'Remove user projects', 'Cancel')
      .subscribe(
        (result) => {
          if (result) {
            for (var i = 0; i < this.userProjects.selection.selected.length; i++) {
              let project = this.userProjects.selection.selected[i];
              this.userProjectsFromDB.forEach((item, index) => {
                if (item === project) {
                  item.deleted = true;
                }
              });
            }
            this.resultData.userProjects = this.userProjectsFromDB;
            this.userService.saveUserProjects(this.resultData.userProjects, this.activeProject.id).subscribe(
              result => {
                this.getUserProjects(this.resultData.uuid);
                this.log.success('User projects removed');
              },
              (error) => this.log.error('User details could not be saved. Please try again.')
            )
          } else {
            this.log.success('Remove cancelled.')
          }
        },
      );
  }
}
