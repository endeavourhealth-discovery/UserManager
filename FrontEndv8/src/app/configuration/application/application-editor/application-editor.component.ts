import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {Router} from "@angular/router";
import {Location} from "@angular/common";
import {UserProject} from "dds-angular8/lib/user-manager/models/UserProject";
import {GenericTableComponent, LoggerService, MessageBoxDialogComponent, UserManagerService} from "dds-angular8";
import {Application} from "../../../models/Application";
import {ApplicationProfile} from "../../../models/ApplicationProfile";
import {ConfigurationService} from "../../configuration.service";
import {MatDialog} from "@angular/material";
import {ApplicationProfileDialogComponent} from "../application-profile-dialog/application-profile-dialog.component";
import {ApplicationDialogComponent} from "../application-dialog/application-dialog.component";

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
  @ViewChild('appProfileTable', { static: false }) appProfileTable: GenericTableComponent;

  public activeProject: UserProject;
  admin = false;
  superUser = false;

  constructor(private log: LoggerService,
              private userManagerNotificationService: UserManagerService,
              private router: Router,
              private location: Location,
              private configurationService: ConfigurationService,
              public dialog: MatDialog) {

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

  editProfile(appProfile: ApplicationProfile) {
    const dialogRef = this.dialog.open(ApplicationProfileDialogComponent, {
      data: {editMode: true, profile: appProfile},
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.appProfileTable.updateRows();
      }
    });
  }

  addProfile() {
    let appProfile = new ApplicationProfile;
    appProfile.applicationId = this.resultApp.id;
    const dialogRef = this.dialog.open(ApplicationProfileDialogComponent, {
      data: {editMode: false, profile: appProfile},
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.log.success('Application policy saved');
        this.applicationProfiles.push(result);
        this.appProfileTable.updateRows();
      }
    });
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

    MessageBoxDialogComponent.open(this.dialog, 'Remove application profiles', 'Are you sure you want to remove the application profiles?',
      'Delete application profiles', 'Cancel')
      .subscribe(
        (result) => {
          if (result) {
            for (var i = 0; i < this.appProfileTable.selection.selected.length; i++) {
              let org = this.appProfileTable.selection.selected[i];
              this.applicationProfiles.forEach((item, index) => {
                if (item === org) {
                  item.isDeleted = true;
                  this.editedProfiles.push(item);
                  this.applicationProfiles.splice(index, 1);
                }
              });
            }
            this.saveProfiles(false);
            this.appProfileTable.updateRows();
          }
        },
      );

  }

  editApplication() {
    const dialogRef = this.dialog.open(ApplicationDialogComponent, {
      minWidth: '50vw',
      data: {application: this.resultApp, editMode: true}
    })
    dialogRef.afterClosed().subscribe(result => {
      return;
    })
  }

}
