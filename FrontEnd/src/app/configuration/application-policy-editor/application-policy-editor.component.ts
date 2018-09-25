import {Component, Input, OnInit} from '@angular/core';
import {UserProject} from "../../user/models/UserProject";
import {LoggerService, MessageBoxDialog, UserManagerService} from "eds-angular4";
import {ModuleStateService} from "eds-angular4/dist/common";
import {Router} from "@angular/router";
import {Location} from "@angular/common";
import {ConfigurationService} from "../configuration.service";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {ApplicationPolicy} from "../models/ApplicationPolicy";
import {ApplicationPolicyAttribute} from "../models/ApplicationPolicyAttribute";
import {Application} from "../models/Application";
import {ApplicationProfile} from "../models/ApplicationProfile";

@Component({
  selector: 'app-role-type-editor',
  templateUrl: './application-policy-editor.component.html',
  styleUrls: ['./application-policy-editor.component.css']
})
export class ApplicationPolicyEditorComponent implements OnInit {

  @Input() resultRole: ApplicationPolicy;
  @Input() editMode: boolean;
  dialogTitle: string = 'Add application policy';

  public activeRole: UserProject;
  admin = false;
  superUsers = false;

  roleProfiles: ApplicationPolicyAttribute[];
  editedProfiles: ApplicationPolicyAttribute[] = [];
  applications: Application[];
  appProfiles: ApplicationProfile[];
  selectedApp: Application;

  constructor(private log: LoggerService,
              private userManagerService: UserManagerService,
              private state: ModuleStateService,
              private router: Router,
              private $modal: NgbModal,
              private location: Location,
              private configurationService: ConfigurationService) { }

  ngOnInit() {
    const vm = this;

    vm.userManagerService.activeRole.subscribe(active => {
      vm.activeRole = active;
      vm.roleChanged();
    });

    vm.getApplications();

    let s = vm.state.getState('roleTypeEdit');
    if (s == null) {
      vm.resultRole = {} as ApplicationPolicy;
      vm.router.navigate(['configuration']);
      return;
    }
    vm.resultRole = Object.assign({}, s.role);
    vm.editMode = s.editMode;
    console.log(vm.resultRole);

    if (vm.editMode) {
      vm.getRoleTypeProfiles();
    } else {
      vm.roleProfiles = [];
    }

    vm.resultRole.isDeleted = false;
    vm.resultRole.jobCategoryId = '';

    if (!vm.editMode) {
      vm.dialogTitle = "Edit application policy";
    }
  }

  roleChanged() {
    const vm = this;
    if (vm.activeRole.applicationPolicyAttributes.find(x => x.applicationAccessProfileName == 'Admin') != null) {
      vm.admin = true;
      vm.superUsers = false;
    } else if (vm.activeRole.applicationPolicyAttributes.find(x => x.applicationAccessProfileName == 'Super User') != null) {
      vm.admin = true;
      vm.superUsers = true;
    } else {
      vm.admin = true;
      vm.superUsers = false;
    }
  }

  getRoleTypeProfiles(){
    let vm = this;
    console.log(vm.resultRole.id);
    vm.configurationService.getRoleTypeAccessProfiles(vm.resultRole.id)
      .subscribe(
        (result) => {
          vm.roleProfiles = result;
          console.log(result);
        },
        (error) => vm.log.error('Loading role type access profiles failed. Please try again', error, 'Error')
      );
  }

  getApplications(){
    let vm = this;
    vm.configurationService.getApplications()
      .subscribe(
        (result) => {
          vm.applications = result;
          if (vm.applications.length > 0) {
            vm.selectedApp = vm.applications[0];
            vm.getApplicationProfiles();
          }
        },
        (error) => vm.log.error('Loading applications failed. Please try again.', error, 'Error')
      );
  }

  getApplicationProfiles(){
    let vm = this;
    vm.appProfiles = null;
    vm.configurationService.getApplicationProfiles(vm.selectedApp.id)
      .subscribe(
        (result) => {
          vm.appProfiles = result;
          console.log(result);
          vm.checkAvailableAppProfiles();
        },
        (error) => vm.log.error('Loading application profiles failed. Please try again', error, 'Error')
      );
  }

  addAvailableProfile(appProfile: ApplicationProfile) {
    const vm = this;
    var newRoleTypeProfile = new ApplicationPolicyAttribute();
    newRoleTypeProfile.isDeleted = false;
    newRoleTypeProfile.applicationPolicyId = vm.resultRole.id;
    newRoleTypeProfile.applicationAccessProfileId = appProfile.id;
    newRoleTypeProfile.applicationAccessProfileName = appProfile.name;
    newRoleTypeProfile.name = appProfile.name;
    newRoleTypeProfile.application = vm.selectedApp.name;
    newRoleTypeProfile.profileTree = '';
    newRoleTypeProfile.applicationId = appProfile.applicationId;

    let i = vm.appProfiles.indexOf(appProfile);
    if (i !== 1) {
      vm.appProfiles.splice(i, 1);
    }

    vm.roleProfiles.push(newRoleTypeProfile);
    vm.editedProfiles.push(newRoleTypeProfile);

  }

  removeAccessProfiles(applicationProfile: ApplicationPolicyAttribute) {
    const vm = this;

    applicationProfile.isDeleted = true;
    vm.editedProfiles.push(applicationProfile);

    vm.getApplicationProfiles();


  }

  checkAvailableAppProfiles() {
    const vm = this;

    if (vm.roleProfiles) {
      for (let profile of vm.roleProfiles) {
        if (!profile.isDeleted && profile.application === vm.selectedApp.name) {
          var profileToDelete = vm.appProfiles.find(e => e.id === profile.applicationAccessProfileId);
          if (profileToDelete != null) {
            let i = vm.appProfiles.indexOf(profileToDelete);
            if (i !== -1) {
              vm.appProfiles.splice(i, 1);
            }
          }
        }
      }
    }
  }

  close(withConfirm: boolean) {
    const vm = this;
    if (withConfirm)
      MessageBoxDialog.open(vm.$modal, vm.dialogTitle, "Any unsaved changes will be lost. Do you want to close without saving?", "Close without saving", "Continue editing")
        .result.then(
        (result) => this.location.back(),
        (reason) => {}
      );
    else
      this.location.back();
  }

  save(close: boolean) {
    const vm = this;
    console.log(vm.resultRole);
    vm.configurationService.saveApplicationPolicy(vm.resultRole, vm.activeRole.id)
      .subscribe(
        (response) => {
          vm.log.success('Application details successfully saved.', null, vm.dialogTitle);
          vm.saveProfiles(close, response);
        },
        (error) => this.log.error('Application details could not be saved. Please try again.', error, 'Save application details')
      );
  }

  saveProfiles(close: boolean, roleTypeId: string) {
    const vm = this;
    if (vm.editedProfiles.length > 0) {
      /*if (vm.resultRole.id != roleTypeId) {
        vm.resultRole.id = roleTypeId;
        vm.editedProfiles.forEach(x => x.applicationPolicyId = roleTypeId);
      }*/
      this.configurationService.saveRoleTypeAccessProfiles(vm.editedProfiles, vm.activeRole.id)
        .subscribe(
          (response) => {
            vm.successfullySavedApplication(close);
          },
          (error) => this.log.error('Application profiles could not be saved. Please try again.', error, 'Save application profiles')
        );
    } else {
      vm.successfullySavedApplication(close);
    }
  }

  successfullySavedApplication(close: boolean) {
    const vm = this;
    this.log.success('Application saved', null, vm.dialogTitle);
    if (close)
      this.close(false);
  }

}
