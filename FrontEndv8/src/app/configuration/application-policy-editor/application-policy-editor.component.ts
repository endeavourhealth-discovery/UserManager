import {Component, OnInit, ViewChild} from '@angular/core';
import {Router} from "@angular/router";
import {Location} from "@angular/common";
import {ConfigurationService} from "../configuration.service";
import {ApplicationPolicy} from "../../models/ApplicationPolicy";
import {UserProject} from "dds-angular8/lib/user-manager/models/UserProject";
import {ApplicationPolicyAttribute} from "../../models/ApplicationPolicyAttribute";
import {Application} from "../../models/Application";
import {ApplicationProfile} from "../../models/ApplicationProfile";
import {GenericTableComponent, LoggerService, MessageBoxDialogComponent, UserManagerService} from "dds-angular8";
import {ApplicationProfilePickerComponent} from "../application/application-profile-picker/application-profile-picker.component";
import {MatDialog} from "@angular/material";
import {ApplicationPolicyDialogComponent} from "../application/application-policy-dialog/application-policy-dialog.component";

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
  appProfiles: ApplicationProfile[];
  selectedApp: Application;

  appPolicyDetailsToShow = new ApplicationPolicyAttribute().getDisplayItems();

  @ViewChild('policyTable', { static: false }) policyTable: GenericTableComponent;

  constructor(private log: LoggerService,
              private userManagerNotificationService: UserManagerService,
              private router: Router,
              private location: Location,
              private configurationService: ConfigurationService,
              public dialog: MatDialog) {

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
          this.policyTable.updateRows();
        },
        (error) => this.log.error('Loading role type access profiles failed. Please try again')
      );
  }

  addAvailableProfile(appProfile: ApplicationProfile) {

    var newRoleTypeProfile = new ApplicationPolicyAttribute();
    newRoleTypeProfile.isDeleted = false;
    newRoleTypeProfile.applicationPolicyId = this.resultPolicy.id;
    newRoleTypeProfile.applicationAccessProfileId = appProfile.id;
    newRoleTypeProfile.applicationAccessProfileName = appProfile.name;
    newRoleTypeProfile.name = appProfile.name;
    newRoleTypeProfile.application = appProfile.applicationName;
    newRoleTypeProfile.applicationAccessProfileSuperUser = appProfile.superUser;
    newRoleTypeProfile.applicationId = appProfile.applicationId;

    this.editedProfiles.push(newRoleTypeProfile);

  }

  removeAccessProfiles() {

    this.editedProfiles = [];

    MessageBoxDialogComponent.open(this.dialog, 'Remove application profiles', 'Are you sure you want to remove application profiles?',
      'Remove application profiles', 'Cancel')
      .subscribe(
        (result) => {
          if(result) {
            for (var i = 0; i < this.policyTable.selection.selected.length; i++) {
              let project = this.policyTable.selection.selected[i];
              this.policyAttributes.forEach( (item, index) => {
                if(item === project) {
                  item.isDeleted = true;
                  this.editedProfiles.push(item);
                }
              });
            }

            console.log('edited', this.editedProfiles);
            console.log('policies', this.policyAttributes);
            this.policyAttributes = this.policyAttributes.filter((x) => !this.editedProfiles.filter(y => y.applicationAccessProfileId === x.applicationAccessProfileId).length);
            this.saveProfiles(false, this.activeProject.id);
          } else {
            this.log.success('Remove cancelled.')
          }
        },
      );
  }

  addAttribute() {
    const dialogRef = this.dialog.open(ApplicationProfilePickerComponent, {
      minWidth: '50vw',
      data: {existing: this.policyAttributes}
    })
    dialogRef.afterClosed().subscribe(result => {
      if (!result) {
        return;
      }
      for (let prof of result) {
        if (!this.editedProfiles.some(x => x.id === prof.id)) {
          this.addAvailableProfile(prof);
        }
      }
      this.saveProfiles(false, this.activeProject.id);
    })
  }

  saveProfiles(close: boolean, roleTypeId: string) {

    if (this.editedProfiles.length > 0) {
      this.configurationService.saveRoleTypeAccessProfiles(this.editedProfiles, this.activeProject.id)
        .subscribe(
          (response) => {
            this.policyAttributes = this.policyAttributes.concat(response);
            this.policyTable.updateRows();
            this.log.success('Application profiles updated.');

          },
          (error) => this.log.error('Application profiles could not be saved. Please try again.')
        );
    }
  }

  editPolicy() {
    const dialogRef = this.dialog.open(ApplicationPolicyDialogComponent, {
      minWidth: '50vw',
      data: {policy: this.resultPolicy, editMode: true}
    })
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.resultPolicy = result;
        return;
      }
    })
  }

}
