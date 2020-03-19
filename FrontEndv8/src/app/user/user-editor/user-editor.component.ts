import {Component, OnInit, ViewChild} from '@angular/core';
import {Location} from '@angular/common';
import {UserProject} from "dds-angular8/lib/user-manager/models/UserProject";
import {User} from "../../models/User";
import {DelegatedOrganisation} from "../../d3-delegation/models/DelegatedOrganisation";
import {ApplicationPolicy} from "../../models/ApplicationPolicy";
import {Project} from "../../models/Project";
import {UserRegion} from "../../models/UserRegion";
import {Region} from "../../models/Region";
import {UserApplicationPolicy} from "../../models/UserApplicationPolicy";
import {GenericTableComponent, LoggerService, UserManagerService} from "dds-angular8";
import {Router} from "@angular/router";
import {UserService} from "../user.service";
import {DelegationService} from "../../d3-delegation/delegation.service";
import {ConfigurationService} from "../../configuration/configuration.service";
import {OrganisationService} from "../../organisation/organisation.service";
import {MatDialog} from "@angular/material/dialog";
import {ProjectPickerComponent} from "../project-picker/project-picker.component";

@Component({
  selector: 'app-user-editor',
  templateUrl: './user-editor.component.html',
  styleUrls: ['./user-editor.component.scss']
})
export class UserEditorComponent implements OnInit {
  resultData: User;
  editMode: boolean;
  existing: boolean;
  selfEdit: boolean;
  dialogTitle: string;
  selectedOrg: DelegatedOrganisation;
  delegatedOrganisations: DelegatedOrganisation[];
  roleTypes: ApplicationPolicy[];
  searchTerm: string;
  searched: boolean = true;
  userList: User[];
  loadingRolesCompleted: boolean = true;
  editedRoles: UserProject[] = [];
  defaultRoleChange : UserProject;
  organisationProjects: Project[];
  userRegion: UserRegion;
  availableRegions: Region[];
  selectedRegion: Region;
  userApplicationPolicy: UserApplicationPolicy;
  availablePolicies: ApplicationPolicy[];
  selectedApplicationPolicy: ApplicationPolicy;
  role: any;
  userProjectsFromDB: UserProject[] = [];

  userDetailsToShow = new User().getDisplayItems();
  userProjectDetailsToShow = new User().getUserProjectDisplayItems();

  activeProject: UserProject;
  admin = false;
  superUser = false;

  @ViewChild('username', { static: false }) usernameBox;
  @ViewChild('forename', { static: false }) forenameBox;
  @ViewChild('surname', { static: false }) surnameBox;
  @ViewChild('email', { static: false }) emailBox;
  @ViewChild('photo', { static: false }) photoURLBox;
  @ViewChild('password1', { static: false }) password1Box;
  @ViewChild('password2', { static: false }) password2Box;
  @ViewChild('searchBox', { static: false }) searchBox;
  @ViewChild('userProjects', { static: false }) userProjects: GenericTableComponent;

  constructor(private log: LoggerService,
              private router: Router,
              private location: Location,
              protected userService: UserService,
              private delegationService: DelegationService,
              private configurationService: ConfigurationService,
              private userManagerNotificationService: UserManagerService,
              private organisationService: OrganisationService,
              public dialog: MatDialog) {

    let s = this.router.getCurrentNavigation().extras.state;

    if (s == null) {
      this.resultData = {} as User;
      this.router.navigate(['user']);
      return;
    }
    this.resultData = s.user;
    this.editMode = s.editMode;
    this.existing = s.existing;
    this.selfEdit = s.selfEdit;
  }

  ngOnInit(): void {

    this.userManagerNotificationService.onProjectChange.subscribe(active => {
      this.activeProject = active;
      this.roleChanged();
    });

    if (!this.editMode) {
      this.dialogTitle = "Add user";

      this.resultData = {
        uuid: null,
        forename: '',
        surname: '',
        username: '',
        password: '',
        email: '',
        mobile: '',
        photo: '',
        defaultOrgId: '',
        userProjects: []
      } as User;
    }
    else {
      this.dialogTitle = "Edit user";

      this.getUserProjects(this.resultData.uuid);

      this.resultData = {
        uuid: this.resultData.uuid,
        forename: this.resultData.forename,
        surname: this.resultData.surname,
        username: this.resultData.username,
        password: '',
        email: this.resultData.email,
        mobile: this.resultData.mobile,
        photo: this.resultData.photo == null ? '': this.resultData.photo,
        defaultOrgId: this.resultData.defaultOrgId == null ? '': this.resultData.defaultOrgId,
        userProjects: this.resultData.userProjects
      } as User;


    }
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

    this.getAvailableRegions();

    if (this.superUser) {
      this.getAvailableApplicationPolicies();
    } else {
      this.getNonSuperUserAvailableApplicationPolicies();
    }
  }

  isEditMode(){
    return this.editMode;
  }

  /*ngAfterViewInit() {
    if (this.existing) {
      this.searchBox.nativeElement.focus();
    } else if (!this.isEditMode()) {
      this.usernameBox.nativeElement.focus();
    }
    else
      this.forenameBox.nativeElement.focus();
  }*/

  save(close: boolean) {

    if (this.validateFormInput() == true) {
      this.userService.saveUser(this.resultData, this.editMode, this.activeProject.id)
        .subscribe(
          (response) => {
            response.password = '';  // blank out password on save
            this.resultData = response;
            this.saveRegion();
            this.saveApplicationPolicy();
            this.saveRoles(close);
          },
          (error) => this.log.error('User details could not be saved. Please try again.')
        );
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

  clear() {
    this.searchTerm = '';
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

  saveRoles(close: boolean) {

    if (!this.editMode) {
      for (let role of this.editedRoles) {
        role.userId = this.resultData.uuid;
      }
    }
    if (this.editedRoles.length > 0) {
      this.userService.saveUserProjects(this.editedRoles, this.activeProject.id)
        .subscribe(
          (response) => {
            if (this.defaultRoleChange) {
              this.changeDefaultProject(close);
            } else {
              this.successfullySavedUser(close);
            }

          },
          (error) => this.log.error('User details could not be saved. Please try again.')
        );
    } else {
      if (this.defaultRoleChange) {
        this.changeDefaultProject(close);
      } else {
        this.successfullySavedUser(close);
      }
    }

  }

  changeDefaultProject(close: boolean) {

    /*this.userManagerService.changeDefaultProject(this.resultData.uuid, this.defaultRoleChange.id, this.activeProject.id)
      .subscribe(
        (result) => {
          this.successfullySavedUser(close);
        },
        (error) => {
          this.log.error('User details could not be saved. Please try again.');
        }
      );*/
  }

  getAvailableRegions() {

    this.userService.getAvailableRegions()
      .subscribe(
        (result) => {
          this.availableRegions = result;
          if (this.editMode && !this.existing) {
            this.getUserRegion();
          }
        },
        (error) => {
          this.log.error('Available regions could not be loaded. Please try again.');
        }
      );
  }

  getAvailableApplicationPolicies() {

    this.configurationService.getApplicationPolicies()
      .subscribe(
        (result) => {
          this.availablePolicies = result;
          if (this.editMode && !this.existing) {
            this.getUserApplicationPolicy();
          }
        },
        (error) => {
          this.log.error('Available application policies could not be loaded. Please try again.');
        }
      );
  }

  getNonSuperUserAvailableApplicationPolicies() {

    this.configurationService.getNonSuperUserApplicationPolicies()
      .subscribe(
        (result) => {
          this.availablePolicies = result;
          if (this.editMode && !this.existing) {
            this.getUserApplicationPolicy();
          }
        },
        (error) => {
          this.log.error('Available application policies could not be loaded. Please try again.');
        }
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
          this.log.error('User region could not be loaded. Please try again.');
        }
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
          this.log.error('User application policy could not be loaded. Please try again.');
        }
      );
  }

  successfullySavedUser(close: boolean) {
    let msg = (!this.editMode) ? 'Add user' : 'Edit user';
    this.log.success('User saved');
    if (close)
      this.close(false);
  }

  close(withConfirm: boolean) {
    /*if (withConfirm)
      MessageBoxDialog.open(this.$modal, "Add user", "Any unsaved changes will be lost. Do you want to close without saving?", "Close without saving", "Continue editing")
        .result.then(
        (result) => this.location.back(),
        (reason) => {}
      );
    else*/
      this.location.back();
  }

  validateFormInput(){
    //go down each tab. check content and flip to and highlight if not complete
    var vm = this;
    var result = true;

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
    } else if (!this.selectedRegion) {
      this.log.error('User region must be selected');
      result = false;
    } else if (!this.selectedApplicationPolicy) {
      this.log.error('User application policy must be selected');
      result = false;
    } else
    if (this.resultData.photo != null && this.resultData.photo.length>100) {
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

  removeCurrentRole(currentRole: UserProject) {
    /*let i = this.resultData.userProjects.indexOf(currentRole);
    if (i !== -1) {
      this.resultData.userProjects.splice(i, 1);
    }
    */

    currentRole.deleted = true;
    this.editedRoles.push(currentRole);

    this.getOrganisationProjects(this.selectedOrg.uuid);

    /*if (currentRole.organisationId == this.selectedOrg.uuid) {
      var newRoleType: ApplicationPolicy = new ApplicationPolicy();
      newRoleType.id = currentRole.projectId;
      newRoleType.name = currentRole.projectName;

      this.roleTypes.push(newRoleType);
    }*/
  }

  //remove from available and add into current, i.e. add into resultData
  addAvailableRole(availableRole: Project) {

    /*var newRole = new UserProject();
    newRole.projectName = availableRole.name;
    newRole.userId = this.resultData.uuid;
    newRole.organisationName = this.selectedOrg.name;
    newRole.projectId = availableRole.uuid;
    newRole.organisationId = this.selectedOrg.uuid;
    newRole.deleted = false;
    newRole.default = false;*/
    let i = this.organisationProjects.indexOf(availableRole);
    if (i !== -1) {
      this.organisationProjects.splice(i, 1);
    }

    // this.resultData.userProjects.push(newRole);
    // this.editedRoles.push(newRole);
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

  getOrganisationProjects(organisationId: string){

    this.organisationService.getProjectsForOrganisation(organisationId)
      .subscribe(
        (result) => {
          this.organisationProjects = result;
          this.checkAvailableProjects()
        },
        (error) => this.log.error('Error loading organisation projects')
      );
  }

  addProject() {
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
          this.userProjects.updateRows();
          this.log.success('User saved');
        },
        (error) => this.log.error('User details could not be saved. Please try again.')
      )
    });
  }

  checkAvailableProjects() {


    /*if (this.selectedOrg.uuid != this.activeProject.organisationId && this.activeProject.projectId != '3517dd59-9ecb-11e8-9245-80fa5b320513') {
      // my organisation is based on my roles but delegated organisations are based on the permissions given to us

      if (!this.selectedOrg.createSuperUsers) {
        var admin = this.roleTypes.findIndex(e => e.id === 'f0bc6f4a-8f18-11e8-839e-80fa5b320513');
        this.roleTypes.splice(admin, 1);
      }

      if (!this.selectedOrg.createUsers) {
        var user = this.roleTypes.findIndex(e => e.id === '00972413-8f50-11e8-839e-80fa5b320513');
        this.roleTypes.splice(user, 1);
      }
    }*/

    /*if (this.selectedOrg.uuid != '439e9f06-d54c-3eb6-b800-010863bf1399' || this.activeProject.projectId != '3517dd59-9ecb-11e8-9245-80fa5b320513') {
      var god = this.roleTypes.findIndex(e => e.id === '3517dd59-9ecb-11e8-9245-80fa5b320513');
      this.roleTypes.splice(god, 1);
    }*/

    if (this.resultData.userProjects) {
      for (let role of this.resultData.userProjects) {
        if (!role.deleted && role.organisationId === this.selectedOrg.uuid) {
          var roleToDelete = this.organisationProjects.find(e => e.uuid === role.projectId);
          if (roleToDelete != null) {
            let i = this.organisationProjects.indexOf(roleToDelete);
            if (i !== -1) {
              this.organisationProjects.splice(i, 1);
            }
          }
        }
      }
    }
  }

  searchUsers() {

    this.searched = false;
    let searchTerm = this.searchTerm.trim().toLowerCase();

    if (searchTerm.length > 2) {
      this.userList = null;
      this.userService.getUsers(null, this.searchTerm)
        .subscribe(
          (result) => {
            this.userList = result;
            this.searched = true;
          },
          (error) => this.log.error('Error loading users and roles')
        );
    }
    this.searched = true;
  }

  clearSearch(){

    this.searched = true;
    this.searchTerm = "";
    this.userList = [];
  }

  selectUser(user: User) {

    this.resultData = user;
    this.resultData.userProjects = [];
    this.resultData.password = '';
    this.existing = false;
  }

  setAsDefaultRole(role: UserProject) {

    this.resultData.userProjects.forEach(x => x.default = (x.id === role.id));
    this.defaultRoleChange = role;
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

}


