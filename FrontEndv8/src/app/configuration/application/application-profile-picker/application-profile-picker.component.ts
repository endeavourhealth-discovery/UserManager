import {Component, Inject, OnInit, ViewChild} from '@angular/core';
import {GenericTableComponent, LoggerService, MessageBoxDialogComponent, UserManagerService} from "dds-angular8";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material";
import {ConfigurationService} from "../../configuration.service";
import {ApplicationProfile} from "../../../models/ApplicationProfile";
import {UserProject} from "dds-angular8/lib/user-manager/models/UserProject";
import {Application} from "../../../models/Application";
import {MatDialog} from "@angular/material/dialog";

export interface DialogData {
  existing: any[];
}

@Component({
  selector: 'app-application-profile-picker',
  templateUrl: './application-profile-picker.component.html',
  styleUrls: ['./application-profile-picker.component.scss']
})
export class ApplicationProfilePickerComponent implements OnInit {

  profiles: ApplicationProfile[] = [];
  profileDetailsToShow = new ApplicationProfile().getDisplayItems();

  public activeProject: UserProject;
  admin = false;
  superUsers = false;

  previousApplication: Application;
  selectedApplication: Application;
  applications: Application[];

  @ViewChild('picker', { static: false }) picker: GenericTableComponent;

  constructor(public dialogRef: MatDialogRef<ApplicationProfilePickerComponent>,
              @Inject(MAT_DIALOG_DATA) public data: DialogData,
              private log: LoggerService,
              private configurationService: ConfigurationService,
              private userManagerNotificationService: UserManagerService,
              public dialog: MatDialog) { }

  ngOnInit() {
    this.userManagerNotificationService.onProjectChange.subscribe(active => {
      this.activeProject = active;
      this.roleChanged();
    });

    this.getApplications();
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

  getApplications() {

    this.configurationService.getApplications()
      .subscribe(
        (result) => {
          this.applications = result;
          if (this.applications.length > 0) {
            this.selectedApplication = this.applications[0];
            this.getApplicationProfiles();
          }
        },
        (error) => this.log.error('Loading applications failed. Please try again.')
      );
  }

  getApplicationProfiles() {
    if (this.picker.selection.selected.length > 0) {
      MessageBoxDialogComponent.open(this.dialog, 'Change organisation', 'This would clear previously selected application profiles?',
        'Change organisation', 'Cancel')
        .subscribe(
          (result) => {
            if (result) {
              this.picker.selection.clear();
              this.previousApplication = this.selectedApplication;
              this.configurationService.getApplicationProfiles(this.selectedApplication.id)
                .subscribe(
                  (result) => {
                    this.profiles = this.filterResults(result);
                    this.picker.updateRows();
                    this.previousApplication = this.selectedApplication;
                  },
                  (error) => this.log.error('Error loading application profiles')
                );
            } else {
              this.selectedApplication = this.previousApplication;
            }
          });
    } else {
      this.configurationService.getApplicationProfiles(this.selectedApplication.id)
        .subscribe(
          (result) => {
            this.profiles = this.filterResults(result);
            this.picker.updateRows();
            this.previousApplication = this.selectedApplication;
          },
          (error) => this.log.error('Error loading application profiles')
        );
    }
  }

  filterResults(results: ApplicationProfile[]) {
    let filterResults: ApplicationProfile[];
    const existing = this.data.existing;
    console.log('existin', this.data.existing);
    console.log('results', results);

    filterResults = results.filter((x) => !existing.filter(y => y.applicationAccessProfileId === x.id).length);

    filterResults.forEach(p => p.applicationName = this.selectedApplication.name);

    if (!this.superUsers) {
      filterResults = filterResults.filter(p => !p.superUser);
    }
    return filterResults;
  }


  ok() {
    this.dialogRef.close(this.picker.selection.selected);
  }

  cancel() {
    this.dialogRef.close();
  }

}
