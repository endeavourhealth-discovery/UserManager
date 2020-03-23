import {Component, OnInit, ViewChild} from '@angular/core';
import {ConfigurationService} from "../configuration.service";
import {Router} from "@angular/router";
import {ApplicationPolicy} from "../../models/ApplicationPolicy";
import {Application} from "../../models/Application";
import {UserProject} from "dds-angular8/lib/user-manager/models/UserProject";
import {GenericTableComponent, LoggerService, MessageBoxDialogComponent, UserManagerService} from "dds-angular8";
import {ApplicationProfile} from "../../models/ApplicationProfile";
import {MatDialog} from "@angular/material/dialog";

@Component({
  selector: 'app-configuration',
  templateUrl: './configuration.component.html',
  styleUrls: ['./configuration.component.css']
})
export class ConfigurationComponent implements OnInit {
  appProfiles: ApplicationPolicy[];
  applications: Application[];

  public data: any;

  public activeProject: UserProject;
  admin = false;
  superUser = false;

  appPolicyDetailsToShow = new ApplicationPolicy().getDisplayItems();
  appDetailsToShow = new Application().getDisplayItems();

  @ViewChild('applicationPoliciesTable', { static: false }) applicationPoliciesTable: GenericTableComponent;
  @ViewChild('applicationsTable', { static: false }) applicationsTable: GenericTableComponent;


  constructor(public log:LoggerService,
              private configurationService : ConfigurationService,
              private userManagerNotificationService: UserManagerService,
              private router: Router,
              public dialog: MatDialog) { }

  ngOnInit() {

    this.userManagerNotificationService.onProjectChange.subscribe(active => {
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

    if (this.superUser) {
      this.getApplicationPolicies();
    } else {
      this.getNonSuperUserApplicationPolicies();
    }
    this.getApplications();
  }

  getApplicationPolicies(){

    this.configurationService.getApplicationPolicies()
      .subscribe(
        (result) => {
          this.appProfiles = result;
        },
        (error) => this.log.error('Loading application policies failed. Please try again')
      );
  }

  getNonSuperUserApplicationPolicies(){

    this.configurationService.getNonSuperUserApplicationPolicies()
      .subscribe(
        (result) => {
          this.appProfiles = result;
        },
        (error) => this.log.error('Loading application policies failed. Please try again')
      );
  }

  getApplications(){

    this.configurationService.getApplications()
      .subscribe(
        (result) => {
          this.applications = result;
        },
        (error) => this.log.error('Loading applications failed. Please try again.')
      );
  }

  addApplication() {
    // this.state.setState('applicationEdit', {application: null, editMode: false});
    this.router.navigate(['appEdit'], {state: {application: null, editMode: false}});
  }

  addApplicationPolicy() {
    // this.state.setState('roleTypeEdit', {role: null, editMode: false});
    this.router.navigate(['appPolicyEdit'], {state: {policy: null, editMode: false}});
  }

  editApp(app: Application) {
    // this.state.setState('applicationEdit', {application: app, editMode: true});
    this.router.navigate(['appEdit'], {state: {application: app, editMode: true}});
  }

  editApplicationPolicy(policy: ApplicationPolicy) {
    // this.state.setState('roleTypeEdit', {role: role, editMode: true});
    this.router.navigate(['appPolicyEdit'], {state: {policy: policy, editMode: true}});
  }

  deleteApplication() {
    MessageBoxDialogComponent.open(this.dialog, 'Delete applications', 'Are you sure you want to remove applications?',
      'Remove applications', 'Cancel')
      .subscribe(
        (result) => {
          if(result) {
            let ids = [];
            for (var i = 0; i < this.applicationsTable.selection.selected.length; i++) {
              let application = this.applicationsTable.selection.selected[i];
              this.applications.forEach( (item, index) => {
                if(item === application) {
                  this.applications.splice(index,1);
                  this.applicationsTable.updateRows();
                  ids.push(item.id);
                }
              });
            }
            this.configurationService.deleteApplication(ids, this.activeProject.id).subscribe(
              () => {
                this.log.success('Successfully deleted applications.');
              }
            );
          } else {
            this.log.success('Remove cancelled.')
          }
        },
      );
  }

  deleteApplicationPolicy() {
    MessageBoxDialogComponent.open(this.dialog, 'Delete application policies', 'Are you sure you want to delete application policies?',
      'Delete application policies', 'Cancel')
      .subscribe(
        (result) => {
          if(result) {
            let ids = [];
            for (var i = 0; i < this.applicationPoliciesTable.selection.selected.length; i++) {
              let applicationPolicy = this.applicationPoliciesTable.selection.selected[i];
              this.appProfiles.forEach( (item, index) => {
                if(item === applicationPolicy) {
                  this.appProfiles.splice(index,1);
                  this.applicationPoliciesTable.updateRows();
                  ids.push(item.id);
                }
              });
            }
            this.configurationService.deleteApplicationPolicy(ids, this.activeProject.id).subscribe(
              () => {
                this.log.success('Successfully deleted application policies.');
              }
            );
          } else {
            this.log.success('Remove cancelled.')
          }
        },
      );
  }
}
