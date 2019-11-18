import { Component, OnInit } from '@angular/core';
import {LoggerService, UserManagerNotificationService} from "eds-angular4";
import {UserProject} from "../../user/models/UserProject";

@Component({
  selector: 'app-audit',
  templateUrl: './audit.component.html',
  styleUrls: ['./audit.component.css']
})
export class AuditComponent implements OnInit {

  public activeProject: UserProject;
  admin = false;
  superUser = false;
  userOrganisationId : string;

  constructor(public log:LoggerService,
              private userManagerNotificationService: UserManagerNotificationService) { }

  ngOnInit() {
    const vm = this;
    this.userManagerNotificationService.activeUserProject.subscribe(active => {
        this.activeProject = active;
        this.roleChanged();
    });

  }

  roleChanged() {
    const vm = this;
    if (vm.activeProject.applicationPolicyAttributes.find(x => x.applicationAccessProfileName == 'Super User') != null) {
      vm.admin = true;
      vm.superUser = true;
      vm.userOrganisationId = null;
    } else if (vm.activeProject.applicationPolicyAttributes.find(x => x.applicationAccessProfileName == 'Admin') != null) {
      vm.admin = true;
      vm.superUser = false;
      vm.userOrganisationId = vm.activeProject.organisationId;
    } else {
      vm.admin = false;
      vm.superUser = false;
      vm.userOrganisationId = vm.activeProject.organisationId;
    }

  }



}
