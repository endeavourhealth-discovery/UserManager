import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {UserRole} from "../../user/models/UserRole";
import {LoggerService, MessageBoxDialog, UserManagerService} from "eds-angular4";
import {ModuleStateService} from "eds-angular4/dist/common";
import {Application} from "../models/Application";
import {Router} from "@angular/router";
import {JsonEditorComponent, JsonEditorOptions} from "angular4-jsoneditor/jsoneditor/jsoneditor.component";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {Location} from "@angular/common";
import {ConfigurationService} from "../configuration.service";
import {ApplicationProfile} from "../models/ApplicationProfile";

@Component({
  selector: 'app-application-editor',
  templateUrl: './application-editor.component.html',
  styleUrls: ['./application-editor.component.css']
})
export class ApplicationEditorComponent implements OnInit {

  @Input() resultApp: Application;
  @Input() editMode: boolean;
  dialogTitle: string = 'Edit application';
  public editorOptions: JsonEditorOptions;
  jsonData: any;
  profileData: any;
  applicationProfiles: ApplicationProfile[];
  selectedProfile: ApplicationProfile;
  editedProfiles: ApplicationProfile[] = [];

  public activeRole: UserRole;
  superUser = false;
  godMode = false;

  @ViewChild(JsonEditorComponent) applicationEditor: JsonEditorComponent;

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

    let s = vm.state.getState('applicationEdit');
    if (s == null) {
      vm.resultApp = {} as Application;
      vm.router.navigate(['configuration']);
      return;
    }
    vm.resultApp = Object.assign({}, s.application);
    vm.editMode = s.editMode;

    vm.resultApp.isDeleted = false;

    if (vm.editMode) {
      vm.getApplicationProfiles();
    }

    vm.jsonData = JSON.parse(vm.resultApp.applicationTree);
    vm.profileData = '';

    this.editorOptions = new JsonEditorOptions();
    this.editorOptions.modes = ['code', 'text', 'tree', 'view'];



    if (!vm.editMode) {
      vm.dialogTitle = "Add application";
    }
  }

  roleChanged() {
    const vm = this;
    if (vm.activeRole.roleTypeId == 'f0bc6f4a-8f18-11e8-839e-80fa5b320513') {
      vm.superUser = true;
      vm.godMode = false;
    } else if (vm.activeRole.roleTypeId == '3517dd59-9ecb-11e8-9245-80fa5b320513') {
      vm.superUser = true;
      vm.godMode = true;
    } else {
      vm.superUser = false;
      vm.godMode = false;
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
    const changedJson = this.applicationEditor.get();
    vm.resultApp.applicationTree = JSON.stringify(changedJson);
    vm.configurationService.saveApplication(vm.resultApp, vm.activeRole.id)
      .subscribe(
        (response) => {
          vm.log.success('Application details successfully saved.', null, vm.dialogTitle);
          vm.saveProfiles(response, close);
        },
        (error) => this.log.error('Application details could not be saved. Please try again.', error, 'Save application details')
      );
  }

  saveProfiles(newApplicationId : string, close: boolean) {
    const vm = this;
    if (vm.editedProfiles.length > 0) {
      vm.editedProfiles.forEach(x => x.applicationId = newApplicationId);
      this.configurationService.saveApplicationProfiles(vm.editedProfiles, vm.activeRole.id)
        .subscribe(
          (response) => {
              vm.successfullySavedApplication(close);
          },
          (error) => this.log.error('Application could not be saved. Please try again.', error, 'Save application profiles')
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

  getApplicationProfiles(){
    let vm = this;
    vm.configurationService.getApplicationProfiles(vm.resultApp.id)
      .subscribe(
        (result) => {
          vm.applicationProfiles = result;
        },
        (error) => vm.log.error('Loading application profiles failed. Please try again', error, 'Error')
      );
  }

  loadJsonForProfile() {
    const vm = this;
    vm.profileData = JSON.parse(vm.selectedProfile.profileTree);
  }

  newProfile() {
    const vm = this;
    let newProfile: ApplicationProfile = new ApplicationProfile();
    newProfile.name = '';
    newProfile.description = '';
    newProfile.applicationId = vm.resultApp.id;
    newProfile.isDeleted = false;
    newProfile.profileTree = '';
    vm.profileData = '';

    vm.applicationProfiles.push(newProfile);
    vm.selectedProfile = newProfile;
  }

  saveProfile() {
    const vm = this;
    const changedJson = this.applicationEditor.get();
    vm.selectedProfile.profileTree = JSON.stringify(changedJson);
    vm.editedProfiles.push(vm.selectedProfile);
  }

  deleteProfile() {
    const vm = this;
    let i = this.applicationProfiles.indexOf(vm.selectedProfile);
    if (i !== -1) {
      this.applicationProfiles.splice(i, 1);
    }
    vm.selectedProfile.isDeleted = true;
    vm.editedProfiles.push(vm.selectedProfile);
  }

}
