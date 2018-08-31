import {Component, OnInit, ViewChild} from '@angular/core';
import {LoggerService, MessageBoxDialog, SecurityService, UserManagerService} from "eds-angular4";
import {ConfigurationService} from "../configuration.service";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {RoleType} from "../models/RoleType";
import {JsonEditorComponent, JsonEditorOptions} from 'angular4-jsoneditor/jsoneditor/jsoneditor.component';
import {Application} from "../models/Application";
import {UserRole} from "../../user/models/UserRole";
import {ModuleStateService} from "eds-angular4/dist/common";
import {Router} from "@angular/router";

@Component({
  selector: 'app-configuration',
  templateUrl: './configuration.component.html',
  styleUrls: ['./configuration.component.css']
})
export class ConfigurationComponent implements OnInit {
  roleTypes: RoleType[];
  applications: Application[];

  public editorOptions: JsonEditorOptions;
  public data: any;

  public activeRole: UserRole;
  superUser = false;
  godMode = false;

  constructor(public log:LoggerService,
              private configurationService : ConfigurationService,
              private securityService : SecurityService,
              private $modal : NgbModal,
              private userManagerService: UserManagerService,
              private state: ModuleStateService,
              private router: Router) { }

  ngOnInit() {
    const vm = this;
    vm.getRoleTypes();
    vm.getApplications();
    this.editorOptions = new JsonEditorOptions();
    this.editorOptions.modes = ['code', 'text', 'tree', 'view'];
    this.data = {"products":[{"name":"car","product":[{"name":"honda","model":[{"id":"civic","name":"civic"},{"id":"accord","name":"accord"},{"id":"crv","name":"crv"},{"id":"pilot","name":"pilot"},{"id":"odyssey","name":"odyssey"}]}]}]}

    this.userManagerService.activeRole.subscribe(active => {
      this.activeRole = active;
      this.roleChanged();
    });

  }

  roleChanged() {
    const vm = this;
    if (vm.activeRole.roleTypeId == 'f0bc6f4a-8f18-11e8-839e-80fa5b320513') {
      vm.superUser = true;
      vm.godMode = false;
    } else if (vm.activeRole.roleTypeId == '3517dd59-9ecb-11e8-9245-80fa5b320513') {
      vm.superUser = true;
      vm.godMode = true;
    } else {
      vm.superUser = false;
      vm.godMode = false;
    }
  }

  getRoleTypes(){
    let vm = this;
    vm.configurationService.getRoleTypes()
      .subscribe(
        (result) => {
          vm.roleTypes = result;
        },
        (error) => vm.log.error('Loading roles failed. Please try again', error, 'Error')
      );
  }

  getApplications(){
    let vm = this;
    vm.configurationService.getApplications()
      .subscribe(
        (result) => {
          vm.applications = result;
        },
        (error) => vm.log.error('Loading applications failed. Please try again.', error, 'Error')
      );
  }

  addApplication() {
    this.state.setState('applicationEdit', {application: null, editMode: false});
    this.router.navigate(['appEdit']);
  }

  editApp(app: Application) {
    this.state.setState('applicationEdit', {application: app, editMode: true});
    this.router.navigate(['appEdit']);
  }

  editRole(role: RoleType) {
    this.state.setState('roleTypeEdit', {role: role, editMode: true});
    this.router.navigate(['roleTypeEdit']);
  }

  deleteApplication(app: Application) {
    const vm = this;
    MessageBoxDialog.open(vm.$modal, "Confirmation", "Delete application: " + app.name + "?", "Yes", "No")
      .result.then(
      (result) => {
        vm.configurationService.deleteApplication(app.id, vm.activeRole.id)
          .subscribe(
            (result) => {
              vm.log.success('Successfully deleted application', null, 'Success');
              vm.getApplications();
            },
            (error) => vm.log.error('Failed to delete application. Please try again', error, 'Error')
          );
      },
      (reason) => {
      }
    )
  }

}
