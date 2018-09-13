import { Component, OnInit } from '@angular/core';
import {LoggerService} from "eds-angular4";
import {AuditService} from "../audit.service";
import {NgbModal, NgbActiveModal, NgbModalOptions } from '@ng-bootstrap/ng-bootstrap';
import {AuditSummary} from "../models/AuditSummary";
import {DisplayDetails} from "../models/DisplayDetails";

@Component({
  selector: 'app-audit-detail',
  templateUrl: './audit-detail.component.html',
  styleUrls: ['./audit-detail.component.css']
})
export class AuditDetailComponent implements OnInit {
  audit: AuditSummary;
  auditDetails: any;
  displayItems: any;

  constructor(public activeModal: NgbActiveModal,
              public log:LoggerService,
              private auditService: AuditService) { }

  ngOnInit() {
    const vm = this;
    vm.displayItems = vm.getDetailsToShow(vm.audit.itemType);
    console.log(vm.displayItems);
    vm.getDetails();
  }

  public static open(modalService: NgbModal, audit: AuditSummary) {
    var options: NgbModalOptions = {
      backdrop: 'static'
    };

    if (audit.auditAction === 'Edit') {
      options = {
        backdrop: 'static',
        size: 'lg'
      }
    }

    const modalRef = modalService.open(AuditDetailComponent, options);
    modalRef.componentInstance.audit = audit;

    return modalRef;
  }



  getDetails() {
    let vm = this;

    console.log(vm.audit);
    vm.auditService.getAuditDetails(vm.audit.id)
      .subscribe(
        (result) => {
          vm.auditDetails = result;
          console.log(result);
        },
        (error) => {
          vm.log.error('Error loading audit details', error, 'Error');
        }
      );
  }

  ok() {
    this.activeModal.close(true);
  }

  cancel() {
    this.activeModal.dismiss(false);
  }

  getDetailsToShow(itemType: string) {
    const dd = new DisplayDetails();
    switch (itemType) {
      case "Role": return dd.getRoleDisplayDetails();
      case "User": return dd.getUserDisplayDetails();
      case "Delegation": return dd.getDelegationDisplayDetails();
      case "Delegation relationship": return dd.getDelegationRelationshipDisplayDetails();
      case "Default role change": return dd.getDefaultRoleChangeDisplayDetails();
      case "Application": return dd.getApplicationDisplayDetails();
      case "Application profile": return dd.getApplicationProfileDisplayDetails();
      case "Role Type access profile": return dd.getRoleTypeAccessProfileDisplayDetails();
      case "User region": return dd.getUserRegionDisplayDetails();
    }
  }

}
