import {AfterViewInit, Component, Input, OnInit, ViewChild, ViewChildren} from '@angular/core';
import {Location} from '@angular/common';
import {User} from "../models/User";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {LoggerService, MessageBoxDialog, UserManagerNotificationService, UserManagerService} from "eds-angular4";
import {UserService} from "../user.service";
import {DelegationService} from "../../d3-delegation/delegation.service";
import {ConfigurationService} from "../../configuration/configuration.service";
import {ApplicationPolicy} from "../../configuration/models/ApplicationPolicy";
import {UserProject} from "../models/UserProject";
import {DelegatedOrganisation} from "../../d3-delegation/models/DelegatedOrganisation";
import {Router} from "@angular/router";
import {ModuleStateService} from 'eds-angular4/dist/common';
import {OrganisationService} from "../../organisation/organisation.service";
import {Project} from "../../configuration/models/Project";
import {UserRegion} from "../models/UserRegion";
import {Region} from "../models/Region";
import {UserApplicationPolicy} from "../models/UserApplicationPolicy";

@Component({
  selector: 'app-user-editor',
  templateUrl: './user-editor.component.html',
  styleUrls: ['./user-editor.component.css']
})
export class UserEditorComponent implements OnInit, AfterViewInit {

  @Input() resultData: User;
  @Input() editMode: boolean;
  @Input() existing: boolean;
  @Input() selfEdit: boolean;
  dialogTitle: String;
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

  public activeProject: UserProject;
  admin = false;
  superUser = false;

  @ViewChild('username') usernameBox;
  @ViewChild('forename') forenameBox;
  @ViewChild('surname') surnameBox;
  @ViewChild('email') emailBox;
  @ViewChild('photo') photoURLBox;
  @ViewChild('password1') password1Box;
  @ViewChild('password2') password2Box;
  @ViewChildren('username') vc;
  @ViewChild('searchBox') searchBox;

  constructor(private log: LoggerService,
              private $modal: NgbModal,
              private router: Router,
              private location: Location,
              protected userService: UserService,
              private delegationService: DelegationService,
              private configurationService: ConfigurationService,
              private state: ModuleStateService,
              private userManagerNotificationService: UserManagerNotificationService,
              private userManagerService: UserManagerService,
              private organisationService: OrganisationService) {

  }

  ngOnInit(): void {
    let s = this.state.getState('userEdit');
    if (s == null) {
      this.resultData = {} as User;
      this.router.navigate(['user']);
      return;
    }

    this.resultData = Object.assign( [], s.user);
    this.editMode = s.editMode;
    this.existing = s.existing;
    this.selfEdit = s.selfEdit;

    let vm = this;
    vm.getAvailableRegions();
    vm.getAvailableApplicationPolicies();

    vm.userManagerNotificationService.activeUserProject.subscribe(active => {
      vm.activeProject = active;
      vm.roleChanged();
    });

    if (!vm.editMode) {
      vm.dialogTitle = "Add user";

      vm.resultData = {
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
      vm.dialogTitle = "Edit user";

      vm.resultData = {
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

  isEditMode(){
    return this.editMode;
  }

  ngAfterViewInit() {
    if (this.existing) {
      this.searchBox.nativeElement.focus();
    } else if (!this.isEditMode()) {
      this.usernameBox.nativeElement.focus();
    }
    else
      this.forenameBox.nativeElement.focus();
  }

  save(close: boolean) {
    const vm = this;
    if (vm.validateFormInput() == true) {
      vm.userService.saveUser(vm.resultData, vm.editMode, vm.activeProject.id)
        .subscribe(
          (response) => {
            vm.resultData = response;
            vm.saveRegion();
            vm.saveApplicationPolicy();
            vm.saveRoles(close);
          },
          (error) => this.log.error('User details could not be saved. Please try again.', error, 'Save user details')
        );
    }
  }

  saveRegion() {
    const vm = this;
    if (vm.userRegion.userId == null) {
      vm.userRegion.userId = vm.resultData.uuid;
    }
    vm.userService.saveUserRegion(vm.userRegion, vm.activeProject.id)
      .subscribe(
        (response) => {

        },
        (error) => vm.log.error('User region could not be saved. Please try again.', error, 'Save user region')
      );
  }

  saveApplicationPolicy() {
    const vm = this;
    if (vm.userApplicationPolicy.userId == null) {
      vm.userApplicationPolicy.userId = vm.resultData.uuid;
    }
    vm.userService.saveUserApplicationPolicy(vm.userApplicationPolicy, vm.activeProject.id)
      .subscribe(
        (response) => {

        },
        (error) => vm.log.error('User application policy could not be saved. Please try again.', error, 'Save user application policy')
      );
  }

  saveRoles(close: boolean) {
    const vm = this;
    if (!vm.editMode) {
      for (let role of vm.editedRoles) {
        role.userId = vm.resultData.uuid;
      }
    }
    if (vm.editedRoles.length > 0) {
      vm.userService.saveUserProjects(vm.editedRoles, vm.activeProject.id)
        .subscribe(
          (response) => {
            if (vm.defaultRoleChange) {
              vm.changeDefaultProject(close);
            } else {
              vm.successfullySavedUser(close);
            }

          },
          (error) => vm.log.error('User details could not be saved. Please try again.', error, 'Save user roles')
        );
    } else {
      if (vm.defaultRoleChange) {
        vm.changeDefaultProject(close);
      } else {
        vm.successfullySavedUser(close);
      }
    }

  }

  changeDefaultProject(close: boolean) {
    const vm = this;
    vm.userManagerService.changeDefaultProject(vm.resultData.uuid, vm.defaultRoleChange.id, vm.activeProject.id)
      .subscribe(
        (result) => {
          vm.successfullySavedUser(close);
        },
        (error) => {
          vm.log.error('User details could not be saved. Please try again.', error, 'Save default role');
        }
      );
  }

  getAvailableRegions() {
    const vm = this;
    vm.userService.getAvailableRegions()
      .subscribe(
        (result) => {
          vm.availableRegions = result;
          if (vm.editMode && !vm.existing) {
            vm.getUserRegion();
          }
        },
        (error) => {
          vm.log.error('Available regions could not be loaded. Please try again.', error, 'Load available regions');
        }
      );
  }

  getAvailableApplicationPolicies() {
    const vm = this;
    vm.configurationService.getApplicationPolicies()
      .subscribe(
        (result) => {
          vm.availablePolicies = result;
          if (vm.editMode && !vm.existing) {
            vm.getUserApplicationPolicy();
          }
        },
        (error) => {
          vm.log.error('Available application policies could not be loaded. Please try again.', error, 'Load available application policies');
        }
      );
  }

  getUserRegion() {
    const vm = this;
    vm.userService.getUserRegion(vm.resultData.uuid)
      .subscribe(
        (result) => {
          vm.userRegion = result;
          vm.selectedRegion = vm.availableRegions.find(r => {
            return r.uuid === vm.userRegion.regionId;
          });
        },
        (error) => {
          vm.log.error('User region could not be loaded. Please try again.', error, 'Load user region');
        }
      );
  }

  getUserApplicationPolicy() {
    const vm = this;
    vm.userService.getUserApplicationPolicy(vm.resultData.uuid)
      .subscribe(
        (result) => {
          vm.userApplicationPolicy = result;
          vm.selectedApplicationPolicy = vm.availablePolicies.find(r => {
            return r.id === vm.userApplicationPolicy.applicationPolicyId;
          });
        },
        (error) => {
          vm.log.error('User application policy could not be loaded. Please try again.', error, 'Load user application policy');
        }
      );
  }

  successfullySavedUser(close: boolean) {
    let msg = (!this.editMode) ? 'Add user' : 'Edit user';
    this.log.success('User saved', null, msg);
    if (close)
      this.close(false);
  }

  close(withConfirm: boolean) {
    if (withConfirm)
      MessageBoxDialog.open(this.$modal, "Add user", "Any unsaved changes will be lost. Do you want to close without saving?", "Close without saving", "Continue editing")
        .result.then(
        (result) => this.location.back(),
        (reason) => {}
      );
    else
      this.location.back();
  }

  validateFormInput(){
    //go down each tab. check content and flip to and highlight if not complete
    var vm = this;
    var result = true;

    //username is mandatory
    if (this.resultData.username.trim() == '') {
      vm.log.warning('Username must not be blank');
      this.usernameBox.nativeElement.focus();
      result = false;
    } else
    //forename is mandatory
    if (this.resultData.forename.trim() == '') {
      vm.log.warning('Forename must not be blank');
      this.forenameBox.nativeElement.focus();
      result = false;
    } else
    //surname is mandatory
    if (this.resultData.surname.trim() == '') {
      vm.log.warning('Surname must not be blank');
      this.surnameBox.nativeElement.focus();
      result = false;
    } else
    //email is mandatory
    if (this.resultData.email.trim() == '') {
      vm.log.warning('Email address must not be blank');
      this.emailBox.nativeElement.focus();
      result = false;
    } else if (!this.selectedRegion) {
      vm.log.warning('User region must be selected');
      result = false;
    } else if (!this.selectedApplicationPolicy) {
      vm.log.warning('User application policy must be selected');
      result = false;
    } else
    if (this.resultData.photo != null && this.resultData.photo.length>100) {
      vm.log.warning('Length of image URL is too long. Consider using bitly or similar to shorten it');
      this.photoURLBox.nativeElement.focus();
      result = false;
    } else
    //check changed passwords match and are valid for a new user addition
    {
      let passwordInput = this.resultData.password.trim();
      if (passwordInput == '') {
        /*if (!this.isEditMode()) {
          vm.log.warning('Password must not be blank');
          this.password1Box.nativeElement.focus();
          result = false;
        }*/
      } else
      //passwords must match and map onto the password policy (1 upper, 1 digit, 8 length and not be the same as username)
      if (this.password2Box.nativeElement.value != this.password1Box.nativeElement.value) {
        vm.log.warning('Passwords must match');
        this.password2Box.nativeElement.focus();
        result = false;
      } else if (passwordInput.length < 8) {
        vm.log.warning('Password must be at least 8 characters long');
        this.password1Box.nativeElement.focus();
        result = false;
      } else if (!/\d/.test(passwordInput)) {
        vm.log.warning('Password must contain at least 1 number');
        this.password1Box.nativeElement.focus();
        result = false;
      } else if (!/[A-Z]/.test(passwordInput)) {
        vm.log.warning('Password must contain at least 1 Uppercase letter');
        this.password1Box.nativeElement.focus();
        result = false;
      } else if (passwordInput == this.resultData.username.trim()) {
        vm.log.warning('Password cannot be the same as username');
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
    const vm = this;
    currentRole.deleted = true;
    vm.editedRoles.push(currentRole);

    vm.getOrganisationProjects(vm.selectedOrg.uuid);

    /*if (currentRole.organisationId == this.selectedOrg.uuid) {
      var newRoleType: ApplicationPolicy = new ApplicationPolicy();
      newRoleType.id = currentRole.projectId;
      newRoleType.name = currentRole.projectName;

      this.roleTypes.push(newRoleType);
    }*/
  }

  //remove from available and add into current, i.e. add into resultData
  addAvailableRole(availableRole: Project) {
    const vm = this;
    var newRole = new UserProject();
    newRole.projectName = availableRole.name;
    newRole.userId = vm.resultData.uuid;
    newRole.organisationName = vm.selectedOrg.name;
    newRole.projectId = availableRole.uuid;
    newRole.organisationId = vm.selectedOrg.uuid;
    newRole.deleted = false;
    newRole.default = false;
    let i = vm.organisationProjects.indexOf(availableRole);
    if (i !== -1) {
      vm.organisationProjects.splice(i, 1);
    }

    vm.resultData.userProjects.push(newRole);
    vm.editedRoles.push(newRole);
  }

  getDelegatedOrganisations() {
    let vm = this;
    vm.delegationService.getDelegatedOrganisations(vm.activeProject.organisationId)
      .subscribe(
        (result) => {
          vm.delegatedOrganisations = result;
          vm.selectedOrg = vm.delegatedOrganisations.find(r => {
            return r.uuid === vm.delegationService.getSelectedOrganisation();
          });
          vm.getOrganisationProjects(vm.selectedOrg.uuid);

        },
        (error) => vm.log.error('Error loading delegated organisations', error, 'Error')
      );
  }

  getGodModeOrganisations() {
    let vm = this;
    vm.delegationService.getGodModeOrganisations()
      .subscribe(
        (result) => {
          vm.delegatedOrganisations = result;
          vm.selectedOrg = vm.delegatedOrganisations.find(r => {
            return r.uuid === vm.delegationService.getSelectedOrganisation();
          });
          vm.getOrganisationProjects(vm.selectedOrg.uuid);

        },
        (error) => vm.log.error('Error loading delegated organisations', error, 'Error')
      );
  }

  getOrganisationProjects(organisationId: string){
    let vm = this;
    vm.organisationService.getProjectsForOrganisation(organisationId)
      .subscribe(
        (result) => {
          vm.organisationProjects = result;
          console.log(result);
          vm.checkAvailableProjects()
        },
        (error) => vm.log.error('Error loading organisation projects', error, 'Load organisation projects')
      );
  }

  checkAvailableProjects() {
    const vm = this;

    /*if (vm.selectedOrg.uuid != vm.activeProject.organisationId && vm.activeProject.projectId != '3517dd59-9ecb-11e8-9245-80fa5b320513') {
      // my organisation is based on my roles but delegated organisations are based on the permissions given to us

      if (!vm.selectedOrg.createSuperUsers) {
        var admin = vm.roleTypes.findIndex(e => e.id === 'f0bc6f4a-8f18-11e8-839e-80fa5b320513');
        vm.roleTypes.splice(admin, 1);
      }

      if (!vm.selectedOrg.createUsers) {
        var user = vm.roleTypes.findIndex(e => e.id === '00972413-8f50-11e8-839e-80fa5b320513');
        vm.roleTypes.splice(user, 1);
      }
    }*/

    /*if (vm.selectedOrg.uuid != '439e9f06-d54c-3eb6-b800-010863bf1399' || vm.activeProject.projectId != '3517dd59-9ecb-11e8-9245-80fa5b320513') {
      var god = vm.roleTypes.findIndex(e => e.id === '3517dd59-9ecb-11e8-9245-80fa5b320513');
      vm.roleTypes.splice(god, 1);
    }*/

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

  searchUsers() {
    let vm = this;
    vm.searched = false;
    let searchTerm = this.searchTerm.trim().toLowerCase();

    if (searchTerm.length > 2) {
      vm.userList = null;
      vm.userService.getUsers(null, vm.searchTerm)
        .subscribe(
          (result) => {
            vm.userList = result;
            vm.searched = true;
          },
          (error) => vm.log.error('Error loading users and roles', error, 'Error')
        );
    }
    vm.searched = true;
  }

  clearSearch(){
    let vm = this;
    vm.searched = true;
    vm.searchTerm = "";
    vm.userList = [];
  }

  selectUser(user: User) {
    const vm = this;
    vm.resultData = user;
    vm.resultData.userProjects = [];
    vm.resultData.password = '';
    vm.existing = false;
  }

  setAsDefaultRole(role: UserProject) {
    const vm = this;
    vm.resultData.userProjects.forEach(x => x.default = (x.id === role.id));
    vm.defaultRoleChange = role;
  }

  changeUserRegion(regionUuid: string) {
    const vm = this;
    let changedRegion = new UserRegion();
    changedRegion.userId = vm.resultData.uuid;
    changedRegion.regionId = regionUuid;
    vm.userRegion = changedRegion;
  }

  changeUserApplicationPolicy(policyId: string) {
    const vm = this;
    let changedPolicy = new UserApplicationPolicy();
    changedPolicy.userId = vm.resultData.uuid;
    changedPolicy.applicationPolicyId = policyId;
    vm.userApplicationPolicy = changedPolicy;
  }

}
