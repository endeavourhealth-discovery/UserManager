import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {Router} from "@angular/router";
import {Location} from "@angular/common";
import {ConfigurationService} from "../configuration.service";
import {Application} from "../../models/Application";
import {ApplicationProfile} from "../../models/ApplicationProfile";
import {UserProject} from "dds-angular8/lib/user-manager/models/UserProject";
import {LoggerService, UserManagerService} from "dds-angular8";

@Component({
  selector: 'app-application-editor',
  templateUrl: './application-editor.component.html',
  styleUrls: ['./application-editor.component.css']
})
export class ApplicationEditorComponent implements OnInit {

  resultApp: Application;
  editMode: boolean;
  dialogTitle: string = 'Edit application';
  jsonData: any;
  profileData: any;
  applicationProfiles: ApplicationProfile[] = [];
  selectedProfile: ApplicationProfile;
  editedProfiles: ApplicationProfile[] = [];

  appAttributeDetailsToShow = new ApplicationProfile().getDisplayItems();

  public activeProject: UserProject;
  admin = false;
  superUser = false;

  // @ViewChild(JsonEditorComponent) applicationEditor: JsonEditorComponent;

  constructor(private log: LoggerService,
              private userManagerNotificationService: UserManagerService,
              private router: Router,
              private location: Location,
              private configurationService: ConfigurationService) {

    let s = this.router.getCurrentNavigation().extras.state;

    if (s == null) {
      this.resultApp = {} as Application;
      this.router.navigate(['configuration']);
      return;
    }
    this.resultApp = s.application;
    this.editMode = s.editMode;
  }

  ngOnInit() {

    this.userManagerNotificationService.onProjectChange.subscribe(active => {
      this.activeProject = active;
      this.roleChanged();
    });

    this.resultApp.isDeleted = false;

    if (this.editMode) {
      this.getApplicationProfiles();
    }

    this.profileData = '';

    if (!this.editMode) {
      this.dialogTitle = "Add application";
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
  }

  close(withConfirm: boolean) {

    /*if (withConfirm)
      MessageBoxDialog.open(this.$modal, this.dialogTitle, "Any unsaved changes will be lost. Do you want to close without saving?", "Close without saving", "Continue editing")
        .result.then(
        (result) => this.location.back(),
        (reason) => {}
      );
    else*/
      this.location.back();
  }

  save(close: boolean) {

    // const changedJson = this.applicationEditor.get();
    this.resultApp.applicationTree = ''; // JSON.stringify(changedJson);
    this.configurationService.saveApplication(this.resultApp, this.activeProject.id)
      .subscribe(
        (response) => {
          this.log.success('Application details successfully saved.');
          this.saveProfiles(close);
        },
        (error) => this.log.error('Application details could not be saved. Please try again.')
      );
  }

  saveProfiles(close: boolean) {

    if (this.editedProfiles.length > 0) {
      this.configurationService.saveApplicationProfiles(this.editedProfiles, this.activeProject.id)
        .subscribe(
          (response) => {
              this.successfullySavedApplication(close);
          },
          (error) => this.log.error('Application could not be saved. Please try again.')
        );
    } else {
        this.successfullySavedApplication(close);
    }
  }

  successfullySavedApplication(close: boolean) {

    this.log.success('Application saved');
    this.editedProfiles = [];
    if (close)
      this.close(false);
  }

  getApplicationProfiles(){

    this.configurationService.getApplicationProfiles(this.resultApp.id)
      .subscribe(
        (result) => {
          this.applicationProfiles = result;
        },
        (error) => this.log.error('Loading application profiles failed. Please try again')
      );
  }

  /*loadJsonForProfile() {

    this.profileData = '';
    console.log(this.selectedProfile.profileTree);
    this.profileData = JSON.parse(this.selectedProfile.profileTree);
    this.applicationEditor.set(JSON.parse(this.selectedProfile.profileTree));
  }*/

  addProfile() {

  }

  newProfile() {

    let newProfile: ApplicationProfile = new ApplicationProfile();
    newProfile.name = '';
    newProfile.description = '';
    newProfile.applicationId = this.resultApp.id;
    newProfile.isDeleted = false;
    newProfile.superUser = false;
    this.profileData = '';

    this.applicationProfiles.push(newProfile);
    this.selectedProfile = newProfile;
  }

  saveProfile() {

    this.editedProfiles.push(this.selectedProfile);
  }

  deleteProfile() {

    /*MessageBoxDialog.open(this.$modal, "Confirmation", "Delete profile: " + this.selectedProfile.name + "?", "Yes", "No")
      .result.then(
      (result) => {
        let i = this.applicationProfiles.indexOf(this.selectedProfile);
        if (i !== -1) {
          this.applicationProfiles.splice(i, 1);
        }
        this.selectedProfile.isDeleted = true;
        this.editedProfiles.push(this.selectedProfile);
      },
      (reason) => {
      }
    )*/
  }

}
