import {Component, Inject, OnInit} from '@angular/core';
import {ItemLinkageService, LoggerService, UserManagerService} from "dds-angular8";
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from "@angular/material/dialog";
import {DatePipe} from "@angular/common";
import {UserProject} from "dds-angular8/lib/user-manager/models/UserProject";
import {ApplicationProfile} from "../../../models/ApplicationProfile";
import {ConfigurationService} from "../../configuration.service";

export interface DialogData {
  profile: ApplicationProfile;
  editMode: boolean;
}

@Component({
  selector: 'app-application-dialog',
  templateUrl: './application-profile-dialog.component.html',
  styleUrls: ['./application-profile-dialog.component.scss']
})
export class ApplicationProfileDialogComponent implements OnInit {
  resultData: ApplicationProfile;
  editMode: boolean;
  activeProject: UserProject;
  admin = false;
  superUser = false;

  constructor(public dialogRef: MatDialogRef<ApplicationProfileDialogComponent>,
              private log: LoggerService,
              @Inject(MAT_DIALOG_DATA) public data: DialogData,
              protected configurationService: ConfigurationService,
              private datePipe: DatePipe,
              private userManagerService: UserManagerService,
              public dialog: MatDialog) {
    this.resultData = data.profile;
    if (!this.resultData) {
      this.resultData = new ApplicationProfile();
    }
    this.editMode = data.editMode;
  }

  ngOnInit() {
    this.userManagerService.onProjectChange.subscribe(active => {
      this.activeProject = active;
      this.roleChanged();
    });
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

  save(close: boolean) {
    this.configurationService.saveApplicationProfile(this.resultData, this.activeProject.id)
      .subscribe(
        (response) => {
          this.dialogRef.close(this.resultData);
          this.log.success('Application profile successfully saved.');
        },
        (error) => this.log.error('Application details could not be saved. Please try again.')
      );
  }

  cancel() {
    this.dialogRef.close();
  }

}
