import {Component, Input, OnInit, ViewChild, ViewChildren} from '@angular/core';
import {User} from "../models/User";
import {NgbModal, NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {LoggerService, MessageBoxDialog} from "eds-angular4";
import {UserService} from "../user.service";
import {Organisation} from "../../organisation/models/Organisation";
import {DelegationService} from "../../delegation/delegation.service";
import {ConfigurationService} from "../../configuration/configuration.service";
import {RoleType} from "../../configuration/models/RoleType";
import {UserRole} from "../models/UserRole";

@Component({
  selector: 'app-user-editor',
  templateUrl: './user-editor.component.html',
  styleUrls: ['./user-editor.component.css']
})
export class UserEditorComponent {

  public static open(modalService: NgbModal, user: User, editMode) {
    const modalRef = modalService.open(UserEditorComponent, {backdrop: "static", size: "lg"});
    modalRef.componentInstance.resultData = Object.assign( [], user);
    modalRef.componentInstance.editMode = editMode;
    modalRef.componentInstance.$modal = modalService;
    return modalRef;
  }

  @Input() resultData: User;
  @Input() editMode: Boolean;
  @Input() $modal: NgbModal;
  dialogTitle: String;
  selectedOrg: Organisation;
  delegatedOrganisations: Organisation[];
  roleTypes: RoleType[];

  @ViewChild('username') usernameBox;
  @ViewChild('forename') forenameBox;
  @ViewChild('surname') surnameBox;
  @ViewChild('email') emailBox;
  @ViewChild('photo') photoURLBox;
  @ViewChild('password1') password1Box;
  @ViewChild('password2') password2Box;
  @ViewChildren('username') vc;

  constructor(private log: LoggerService,
              protected activeModal: NgbActiveModal,
              protected userService: UserService,
              private delegationService: DelegationService,
              private configurationService: ConfigurationService) {

  }

  ngOnInit(): void {
    let vm = this;
    vm.getDelegatedOrganisations();
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
        userRoles: []
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
        userRoles: this.resultData.userRoles
      } as User;
    }



  }

  isEditMode(){
    return this.editMode;
  }

  ngAfterViewInit() {
    if (!this.isEditMode()) {
      this.usernameBox.nativeElement.focus();
    }
    else
      this.forenameBox.nativeElement.focus();
  }

  save() {
    if (this.validateFormInput() == true) {
      this.activeModal.close(this.resultData);
    }
  }

  cancel() {
    MessageBoxDialog.open(this.$modal, "Confirmation", "Are you sure you want to cancel?", "Yes", "No")
      .result.then(
      (result) => {
        this.activeModal.dismiss('cancel');
      },
      (reason) => {}
    );
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
        if (!this.isEditMode()) {
          vm.log.warning('Password must not be blank');
          this.password1Box.nativeElement.focus();
          result = false;
        }
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

  removeCurrentRole(currentRole: UserRole) {
    let i = this.resultData.userRoles.indexOf(currentRole);
    if (i !== -1) {
      this.resultData.userRoles.splice(i, 1);
    }

    var newRoleType: RoleType = new RoleType();
    newRoleType.id = currentRole.roleTypeId;
    newRoleType.name = currentRole.roleTypeName;

    this.roleTypes.push(newRoleType);
  }

  //remove from available and add into current, i.e. add into resultData
  addAvailableRole(availableRole: RoleType) {
    const vm = this;
    var newRole = new UserRole();
    newRole.roleTypeName = availableRole.name;
    newRole.userId = vm.resultData.uuid;
    newRole.organisationName = vm.selectedOrg.name;
    newRole.roleTypeId = availableRole.id;
    newRole.organisationId = vm.selectedOrg.uuid;
    newRole.deleted = false;
    newRole.userAccessProfileId = '3242343432323';
    let i = vm.roleTypes.indexOf(availableRole);
    if (i !== -1) {
      vm.roleTypes.splice(i, 1);
    }

    vm.resultData.userRoles.push(newRole);
  }

  getDelegatedOrganisations() {
    let vm = this;
    vm.delegationService.getDelegatedOrganisations(vm.delegationService.getSelectedOrganisation(), vm.delegationService.getSelectedDelegation())
      .subscribe(
        (result) => {
          vm.delegatedOrganisations = result;
          vm.selectedOrg = vm.delegatedOrganisations.find(r => {
            return r.uuid === vm.delegationService.getSelectedOrganisation();
          });
          vm.getRoleTypes();

          console.log(result);
        },
        (error) => vm.log.error('Error loading delegated organisations', error, 'Error')
      );
  }

  getRoleTypes(){
    let vm = this;
    vm.configurationService.getRoleTypes()
      .subscribe(
        (result) => {
          vm.roleTypes = result;
          vm.checkAvailableRoles()
        },
        (error) => vm.log.error('Error loading users and roles', error, 'Error')
      );
  }

  checkAvailableRoles() {
    const vm = this;
    for (let role of vm.resultData.userRoles) {
      if (role.organisationId === vm.selectedOrg.uuid) {
        var roleToDelete = vm.roleTypes.find(e => e.id === role.roleTypeId);
        console.log(roleToDelete);
        if (roleToDelete != null) {
          let i = vm.roleTypes.indexOf(roleToDelete);
          console.log(i);
          if (i !== -1) {
            vm.roleTypes.splice(i, 1);
            console.log('deleted', vm.roleTypes);
          }
        }
      }
    }
  }

}
