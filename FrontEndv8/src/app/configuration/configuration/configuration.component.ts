import {Component, OnInit, ViewChild} from '@angular/core';
import {ConfigurationService} from "../configuration.service";
import {Router} from "@angular/router";
import {ApplicationPolicy} from "../../models/ApplicationPolicy";
import {Application} from "../../models/Application";
import {UserProject} from "dds-angular8/lib/user-manager/models/UserProject";
import {LoggerService, UserManagerService} from "dds-angular8";
import {ApplicationProfile} from "../../models/ApplicationProfile";

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

  constructor(public log:LoggerService,
              private configurationService : ConfigurationService,
              private userManagerNotificationService: UserManagerService,
              private router: Router) { }

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

  deleteApplication(app: Application) {

    /*MessageBoxDialog.open(this.$modal, "Confirmation", "Delete application: " + app.name + "?", "Yes", "No")
      .result.then(
      (result) => {
        this.configurationService.deleteApplication(app.id, this.activeProject.id)
          .subscribe(
            (result) => {
              this.log.success('Successfully deleted application');
              this.getApplications();
            },
            (error) => this.log.error('Failed to delete application. Please try again')
          );
      },
      (reason) => {
      }
    )*/
  }

  deleteApplicationPolicy(appProfile: ApplicationProfile) {

    /*MessageBoxDialog.open(this.$modal, "Confirmation", "Delete application policy: " + appProfile.name + "?", "Yes", "No")
      .result.then(
      (result) => {
        this.configurationService.deleteApplicationPolicy(appProfile.id, this.activeProject.id)
          .subscribe(
            (result) => {
              this.log.success('Successfully deleted application policy', null, 'Delete application policy');
              this.getApplicationPolicies();
            },
            (error) => this.log.error('Failed to delete application policy. Please try again', error, 'Delete application policy')
          );
      },
      (reason) => {
      }
    )*/
  }

}
