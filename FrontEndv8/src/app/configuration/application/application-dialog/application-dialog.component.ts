import {Component, Inject, OnInit} from '@angular/core';
import {Application} from "../../../models/Application";
import {LoggerService, UserManagerService} from "dds-angular8";
import {ConfigurationService} from "../../configuration.service";
import {UserProject} from "dds-angular8/lib/user-manager/models/UserProject";
import {DatePipe} from "@angular/common";
import {ApplicationProfileDialogComponent} from "../application-profile-dialog/application-profile-dialog.component";
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from "@angular/material/dialog";

export interface DialogData {
  application: Application;
  editMode: boolean;
}

@Component({
  selector: 'app-application-dialog',
  templateUrl: './application-dialog.component.html',
  styleUrls: ['./application-dialog.component.scss']
})
export class ApplicationDialogComponent implements OnInit {
  resultData: Application;
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
    this.resultData = new Application();
    if (data.application) {
      this.resultData = new Application();
      this.resultData.id = data.application.id;
      this.resultData.isDeleted = data.application.isDeleted;
      this.resultData.applicationTree = data.application.applicationTree;
      this.resultData.description = data.application.description;
      this.resultData.name = data.application.name;
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

  save() {

    this.resultData.applicationTree = '';
    this.configurationService.saveApplication(this.resultData, this.activeProject.id)
      .subscribe(
        (response) => {
          this.log.success('Application details successfully saved.');
          this.dialogRef.close(response);
        },
        (error) => this.log.error('Application details could not be saved. Please try again.')
      );
  }

  cancel() {
    this.dialogRef.close();
  }

}
