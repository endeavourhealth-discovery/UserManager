import {AfterViewInit, Component, Input, OnInit, ViewChild, ViewChildren} from '@angular/core';
import {Location} from '@angular/common';
import {User} from "../models/User";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {LoggerService, MessageBoxDialog, UserManagerService} from "eds-angular4";
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

  public activeRole: UserProject;
  superUser = false;
  godMode = false;

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

    vm.userManagerService.activeRole.subscribe(active => {
      vm.activeRole = active;
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

    if (vm.godMode) {
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
      vm.userService.saveUser(vm.resultData, vm.editMode, vm.activeRole.id)
        .subscribe(
          (response) => {
            vm.resultData = response;
            this.saveRoles(close);
          },
          (error) => this.log.error('User details could not be saved. Please try again.', error, 'Save user details')
        );
    }
  }

  saveRoles(close: boolean) {
    const vm = this;
    if (!vm.editMode) {
      for (let role of vm.editedRoles) {
        role.userId = vm.resultData.uuid;
      }
    }
    if (vm.editedRoles.length > 0) {
      this.userService.saveUserProjects(vm.editedRoles, vm.activeRole.id)
        .subscribe(
          (response) => {
            if (vm.defaultRoleChange) {
              vm.changeDefaultProject(close);
            } else {
              vm.successfullySavedUser(close);
            }

          },
          (error) => this.log.error('User details could not be saved. Please try again.', error, 'Save user roles')
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
    vm.userManagerService.changeDefaultProject(vm.resultData.uuid, vm.defaultRoleChange.id, vm.activeRole.id)
      .subscribe(
        (result) => {
          vm.successfullySavedUser(close);
        },
        (error) => {
          vm.log.error('User details could not be saved. Please try again.', error, 'Save default role');
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
    vm.delegationService.getDelegatedOrganisations(vm.activeRole.organisationId)
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

    /*if (vm.selectedOrg.uuid != vm.activeRole.organisationId && vm.activeRole.projectId != '3517dd59-9ecb-11e8-9245-80fa5b320513') {
      // my organisation is based on my roles but delegated organisations are based on the permissions given to us

      if (!vm.selectedOrg.createSuperUsers) {
        var superUser = vm.roleTypes.findIndex(e => e.id === 'f0bc6f4a-8f18-11e8-839e-80fa5b320513');
        vm.roleTypes.splice(superUser, 1);
      }

      if (!vm.selectedOrg.createUsers) {
        var user = vm.roleTypes.findIndex(e => e.id === '00972413-8f50-11e8-839e-80fa5b320513');
        vm.roleTypes.splice(user, 1);
      }
    }*/

    /*if (vm.selectedOrg.uuid != '439e9f06-d54c-3eb6-b800-010863bf1399' || vm.activeRole.projectId != '3517dd59-9ecb-11e8-9245-80fa5b320513') {
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

}
