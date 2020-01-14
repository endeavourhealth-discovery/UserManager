import {Component, Input, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {Location} from "@angular/common";
import {ConfigurationService} from "../configuration.service";
import {ApplicationPolicy} from "../../models/ApplicationPolicy";
import {UserProject} from "dds-angular8/lib/user-manager/models/UserProject";
import {ApplicationPolicyAttribute} from "../../models/ApplicationPolicyAttribute";
import {Application} from "../../models/Application";
import {ApplicationProfile} from "../../models/ApplicationProfile";
import {LoggerService, UserManagerService} from "dds-angular8";

@Component({
  selector: 'app-role-type-editor',
  templateUrl: './application-policy-editor.component.html',
  styleUrls: ['./application-policy-editor.component.css']
})
export class ApplicationPolicyEditorComponent implements OnInit {

  resultPolicy: ApplicationPolicy;
  editMode: boolean;
  dialogTitle: string = 'Add application policy';

  public activeProject: UserProject;
  admin = false;
  superUsers = false;

  policyAttributes: ApplicationPolicyAttribute[];
  editedProfiles: ApplicationPolicyAttribute[] = [];
  applications: Application[];
  appProfiles: ApplicationProfile[];
  selectedApp: Application;

  appPolicyDetailsToShow = new ApplicationPolicyAttribute().getDisplayItems();

  constructor(private log: LoggerService,
              private userManagerNotificationService: UserManagerService,
              private router: Router,
              private location: Location,
              private configurationService: ConfigurationService) {

    let s = this.router.getCurrentNavigation().extras.state;

    if (s == null) {
      this.resultPolicy = {} as ApplicationPolicy;
      this.router.navigate(['configuration']);
      return;
    }
    this.resultPolicy = s.policy;
    this.editMode = s.editMode;
  }

  ngOnInit() {


    this.userManagerNotificationService.onProjectChange.subscribe(active => {
      this.activeProject = active;
      this.roleChanged();
    });

    this.getApplications();

    if (this.editMode) {
      this.getRoleTypeProfiles();
    } else {
      this.policyAttributes = [];
    }

    this.resultPolicy.isDeleted = false;
    this.resultPolicy.jobCategoryId = '';

    if (!this.editMode) {
      this.dialogTitle = "Edit application policy";
    }
  }

  roleChanged() {

    if (this.activeProject.applicationPolicyAttributes.find(x => x.applicationAccessProfileName == 'Super User') != null) {
      this.admin = true;
      this.superUsers = true;
    } else if (this.activeProject.applicationPolicyAttributes.find(x => x.applicationAccessProfileName == 'Admin') != null) {
      this.admin = true;
      this.superUsers = false;
    } else {
      this.admin = false;
      this.superUsers = false;
    }
  }

  getRoleTypeProfiles(){

    this.configurationService.getRoleTypeAccessProfiles(this.resultPolicy.id)
      .subscribe(
        (result) => {
          this.policyAttributes = result;
        },
        (error) => this.log.error('Loading role type access profiles failed. Please try again')
      );
  }

  getApplications(){

    this.configurationService.getApplications()
      .subscribe(
        (result) => {
          this.applications = result;
          if (this.applications.length > 0) {
            this.selectedApp = this.applications[0];
            this.getApplicationProfiles();
          }
        },
        (error) => this.log.error('Loading applications failed. Please try again.')
      );
  }

  getApplicationProfiles(){

    this.appProfiles = null;
    this.configurationService.getApplicationProfiles(this.selectedApp.id)
      .subscribe(
        (result) => {
          this.appProfiles = result;
          this.checkAvailableAppProfiles();
        },
        (error) => this.log.error('Loading application profiles failed. Please try again')
      );
  }

  addAvailableProfile(appProfile: ApplicationProfile) {

    var newRoleTypeProfile = new ApplicationPolicyAttribute();
    newRoleTypeProfile.isDeleted = false;
    newRoleTypeProfile.applicationPolicyId = this.resultPolicy.id;
    newRoleTypeProfile.applicationAccessProfileId = appProfile.id;
    newRoleTypeProfile.applicationAccessProfileName = appProfile.name;
    newRoleTypeProfile.name = appProfile.name;
    newRoleTypeProfile.application = this.selectedApp.name;
    newRoleTypeProfile.applicationAccessProfileSuperUser = appProfile.superUser;
    newRoleTypeProfile.applicationId = appProfile.applicationId;

    let i = this.appProfiles.indexOf(appProfile);
    if (i !== 1) {
      this.appProfiles.splice(i, 1);
    }

    this.policyAttributes.push(newRoleTypeProfile);
    this.editedProfiles.push(newRoleTypeProfile);

  }

  removeAccessProfiles(applicationProfile: ApplicationPolicyAttribute) {


    applicationProfile.isDeleted = true;
    this.editedProfiles.push(applicationProfile);

    this.getApplicationProfiles();

  }

  addAttribute() {

  }

  checkAvailableAppProfiles() {


    if (!this.superUsers) {
      this.appProfiles = this.appProfiles.filter(p => !p.superUser);
    }

    if (this.policyAttributes) {
      for (let profile of this.policyAttributes) {
        if (!profile.isDeleted && profile.application === this.selectedApp.name) {
          var profileToDelete = this.appProfiles.find(e => e.id === profile.applicationAccessProfileId);
          if (profileToDelete != null) {
            let i = this.appProfiles.indexOf(profileToDelete);
            if (i !== -1) {
              this.appProfiles.splice(i, 1);
            }
          }
        }
      }
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

    this.configurationService.saveApplicationPolicy(this.resultPolicy, this.activeProject.id)
      .subscribe(
        (response) => {
          this.log.success('Application details successfully saved.');
          this.saveProfiles(close, response);
        },
        (error) => this.log.error('Application details could not be saved. Please try again.')
      );
  }

  saveProfiles(close: boolean, roleTypeId: string) {

    if (this.editedProfiles.length > 0) {
      /*if (this.resultPolicy.id != roleTypeId) {
        this.resultPolicy.id = roleTypeId;
        this.editedProfiles.forEach(x => x.applicationPolicyId = roleTypeId);
      }*/
      this.configurationService.saveRoleTypeAccessProfiles(this.editedProfiles, this.activeProject.id)
        .subscribe(
          (response) => {
            this.successfullySavedApplication(close);
          },
          (error) => this.log.error('Application profiles could not be saved. Please try again.')
        );
    } else {
      this.successfullySavedApplication(close);
    }
  }

  successfullySavedApplication(close: boolean) {

    this.log.success('Application saved');
    if (close)
      this.close(false);
  }

}
