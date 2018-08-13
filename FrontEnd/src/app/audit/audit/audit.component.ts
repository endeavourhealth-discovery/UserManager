import { Component, OnInit } from '@angular/core';
import {AuditSummary} from "../models/AuditSummary";
import {LoggerService, UserManagerService} from "eds-angular4";
import {AuditService} from "../audit.service";
import {AuditDetailComponent} from "../audit-detail/audit-detail.component";
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {DelegatedOrganisation} from "../../d3-delegation/models/DelegatedOrganisation";
import {DelegationService} from "../../d3-delegation/delegation.service";
import {User} from "../../user/models/User";
import {UserService} from "../../user/user.service";
import {UserRole} from "../../user/models/UserRole";

@Component({
  selector: 'app-audit',
  templateUrl: './audit.component.html',
  styleUrls: ['./audit.component.css']
})
export class AuditComponent implements OnInit {
  auditSummaries: AuditSummary[];
  loadingComplete = false;
  totalItems = 5;
  pageNumber = 1;
  pageSize = 15;
  delegatedOrganisations: DelegatedOrganisation[];
  selectedOrg: DelegatedOrganisation;
  userList: User[];
  selectedUser: User;
  filtered = false;
  dateFrom: Date = new Date();
  dateTo: Date = new Date();

  public activeRole: UserRole;
  superUser = false;
  godMode = false;

  settings = {
    bigBanner: true,
    timePicker: true,
    format: 'dd-MM-yyyy hh:mm:ss',
    defaultOpen: false
  };

  constructor(private $modal: NgbModal,
              public log:LoggerService,
              private auditService: AuditService,
              private delegationService: DelegationService,
              private userService: UserService,
              private userManagerService: UserManagerService) { }

  ngOnInit() {
    const vm = this;
    this.userManagerService.activeRole.subscribe(active => {
      this.activeRole = active;
      this.roleChanged();
    });

  }

  roleChanged() {
    const vm = this;
    if (vm.activeRole.roleTypeId == 'f0bc6f4a-8f18-11e8-839e-80fa5b320513') {
      vm.superUser = true;
    } else if (vm.activeRole.roleTypeId == '3517dd59-9ecb-11e8-9245-80fa5b320513') {
      vm.superUser = true;
      vm.godMode = true;
    } else {
      vm.superUser = false;
      vm.godMode = false;
    }

    vm.getAudit();
    vm.getAuditCount();
    if (vm.godMode) {
      vm.getGodModeOrganisations();
    } else {
      vm.getDelegatedOrganisations();
    }
  }

  getAudit(){
    let vm = this;
    vm.loadingComplete = false;
    let orgId = null;
    let usrId = null;
    let fromDate = null;
    let toDate = null
    if (vm.filtered) {
      if (vm.selectedOrg != null) {
        orgId = vm.selectedOrg.uuid;
      }
      if (vm.selectedUser != null) {
        usrId = vm.selectedUser.uuid;
      }
      fromDate = vm.dateFrom;
      toDate = vm.dateTo;
    }
    vm.auditService.getAuditSummary(vm.godMode ? null : vm.activeRole.organisationId, vm.pageNumber, vm.pageSize, orgId, usrId, fromDate, toDate)
      .subscribe(
        (result) => {
          vm.auditSummaries = result;
          vm.loadingComplete = true;
        },
        (error) => {
          vm.log.error('Error loading audit data', error, 'Error');
          vm.loadingComplete = true;
        }
      );
  }

  getAuditCount() {
    const vm = this; let orgId = null;
    let usrId = null;
    if (vm.filtered) {
      orgId = vm.selectedOrg.uuid;
      usrId = vm.selectedUser.uuid;
    }
    vm.auditService.getAuditCount(orgId, usrId)
      .subscribe(
        (result) => {
          vm.totalItems = result;
        },
        (error) => console.log(error)
      );
  }

  showDetails(audit: AuditSummary) {
    const vm = this;
    AuditDetailComponent.open(vm.$modal, audit)
      .result.then(function
        (result: boolean) {
          return;
    })
  }

  pageChanged($event) {
    const vm = this;
    vm.pageNumber = $event;
    console.log(vm.pageNumber);
    vm.getAudit();
  }

  onDateSelect($event) {
    const vm = this;
    console.log($event);
    console.log(typeof vm.dateTo);
    console.log(vm.dateFrom);
    console.log();
  }

  getDelegatedOrganisations(){
    let vm = this;
    vm.delegationService.getDelegatedOrganisations(vm.activeRole.organisationId)
      .subscribe(
        (result) => {
          vm.delegatedOrganisations = result;
        },
        (error) => vm.log.error('Error loading delegated organisations', error, 'Error')
      );
  }

  getGodModeOrganisations(){
    let vm = this;
    vm.delegationService.getGodModeOrganisations()
      .subscribe(
        (result) => {
          vm.delegatedOrganisations = result;
        },
        (error) => vm.log.error('Error loading delegated organisations', error, 'Error')
      );
  }

  //gets all users in the selected organisation
  getUsers(){
    let vm = this;
    vm.userList = null;
    vm.userService.getUsers(vm.selectedOrg.uuid)
      .subscribe(
        (result) => {
          vm.userList = result;
        },
        (error) => vm.log.error('Error loading users and roles', error, 'Error')
      );
  }

  reset() {
    const vm = this;
    vm.selectedOrg = null;
    vm.selectedUser = null;
    vm.filtered = false;
  }

  filter() {
    const vm = this;
    vm.filtered = true;
    vm.getAudit();
  }

}
