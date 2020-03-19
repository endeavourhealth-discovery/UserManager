import {Component, Inject, OnInit, ViewChild} from '@angular/core';
import {GenericTableComponent, LoggerService, MessageBoxDialogComponent} from "dds-angular8";
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from "@angular/material/dialog";
import {DelegatedOrganisation} from "../../d3-delegation/models/DelegatedOrganisation";
import {OrganisationService} from "../../organisation/organisation.service";
import {Project} from "../../models/Project";
import { UserProject_local } from 'src/app/models/UserProject_local';

export interface DialogData {
  delegatedOrganisations: DelegatedOrganisation[];
  userId: string;
}

@Component({
  selector: 'app-project-picker',
  templateUrl: './project-picker.component.html',
  styleUrls: ['./project-picker.component.scss']
})
export class ProjectPickerComponent implements OnInit {

  selectedOrganisation: DelegatedOrganisation;
  delegatedOrganisations: DelegatedOrganisation[];
  userProjects: UserProject_local[];
  projects: Project[];
  projectDetailsToShow = new Project().getProjectDisplayItems();
  userId: string;

  @ViewChild('picker', { static: false }) picker: GenericTableComponent;

  constructor(public dialogRef: MatDialogRef<ProjectPickerComponent>,
              @Inject(MAT_DIALOG_DATA) public data: DialogData,
              private log: LoggerService,
              public dialog: MatDialog,
              private organisationService: OrganisationService) {

    this.delegatedOrganisations = data.delegatedOrganisations;
    this.userId = data.userId;
  }

  ngOnInit() {
  }

  getOrganisationProjects() {
    if (this.picker.selection.selected.length > 0) {
      MessageBoxDialogComponent.open(this.dialog, 'Change organisation', 'This would disregard previously selected projects?',
        'Change organisation', 'Cancel')
        .subscribe(
          (result) => {
            this.picker.selection = null;
          });
    }
    this.organisationService.getProjectsForOrganisation(this.selectedOrganisation.uuid)
      .subscribe(
        (result) => {
          this.projects = result;
          this.picker.updateRows();
        },
        (error) => this.log.error('Error loading organisation projects')
      );
  }

  ok() {
    for (let proj of this.picker.selection.selected) {
      var project = new UserProject_local();
      project.userId = this.userId;
      project.organisationId = this.selectedOrganisation.uuid;
      project.organisationName = this.selectedOrganisation.name;
      project.projectId = proj.uuid;
      project.projectName = proj.name;
      project.deleted = false;
      project.default = false;
      if (this.userProjects) {
        this.userProjects.push(project);
      } else {
        this.userProjects = [];
        this.userProjects.push(project);
      }
    }
    this.dialogRef.close(this.userProjects);
  }

  cancel() {
    this.dialogRef.close();
  }
}
