import {Component, Inject, OnInit} from '@angular/core';
import {User} from "../../models/User";
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from "@angular/material/dialog";
import {ItemLinkageService, LoggerService, UserManagerService} from "dds-angular8";
import {DatePipe} from "@angular/common";
import {UserService} from "../user.service";
import {UserProject} from "dds-angular8/lib/user-manager/models/UserProject";

export interface DialogData {
  user: User;
  editMode: boolean;
}

@Component({
  selector: 'app-user-dialog',
  templateUrl: './user-dialog.component.html',
  styleUrls: ['./user-dialog.component.scss']
})
export class UserDialogComponent implements OnInit {

  resultData: User;
  editMode: boolean;
  activeProject: UserProject;
  admin = false;
  superUser = false;

  constructor(public dialogRef: MatDialogRef<UserDialogComponent>,
              private log: LoggerService,
              @Inject(MAT_DIALOG_DATA) public data: DialogData,
              protected userService: UserService,
              private datePipe: DatePipe,
              private userManagerService: UserManagerService,
              public dialog: MatDialog,
              private linkageService: ItemLinkageService) {
    this.resultData = data.user;
    if (!this.resultData) {
      this.resultData = new User();
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

  ok() {
    this.userService.saveUser(this.resultData, this.editMode, this.activeProject.id)
      .subscribe(
        (response) => {
          response.password = '';  // blank out password on save
          this.resultData = response;
          this.dialogRef.close(this.resultData);
        },
        (error) => this.log.error('User details could not be saved. Please try again.')
      );
  }

  cancel() {
    this.dialogRef.close();
  }
}
