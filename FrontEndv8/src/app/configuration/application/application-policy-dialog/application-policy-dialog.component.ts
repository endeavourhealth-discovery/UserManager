import {Component, Inject, OnInit} from '@angular/core';
import {LoggerService, UserManagerService} from "dds-angular8";
import {ApplicationProfileDialogComponent} from "../application-profile-dialog/application-profile-dialog.component";
import {ConfigurationService} from "../../configuration.service";
import {UserProject} from "dds-angular8/lib/user-manager/models/UserProject";
import {DatePipe} from "@angular/common";
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from "@angular/material/dialog";
import {ApplicationPolicy} from "../../../models/ApplicationPolicy";

export interface DialogData {
  policy: ApplicationPolicy;
  editMode: boolean;
}

@Component({
  selector: 'app-application-policy-dialog',
  templateUrl: './application-policy-dialog.component.html',
  styleUrls: ['./application-policy-dialog.component.scss']
})
export class ApplicationPolicyDialogComponent implements OnInit {
  resultData: ApplicationPolicy;
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
    this.resultData = data.policy;
    if (!this.resultData) {
      this.resultData = new ApplicationPolicy();
      this.resultData.jobCategoryId = '';
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

    this.configurationService.saveApplicationPolicy(this.resultData, this.activeProject.id)
      .subscribe(
        (response) => {
          this.log.success('Application policy details successfully saved.');
          this.dialogRef.close(response);
        },
        (error) => this.log.error('Application policy details could not be saved. Please try again.')
      );
  }

  cancel() {
    this.dialogRef.close();
  }

}
